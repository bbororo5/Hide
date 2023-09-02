package com.example.backend.user.service;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.backend.user.dto.TokenDto;
import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.entity.RefreshToken;
import com.example.backend.user.entity.User;
import com.example.backend.util.UserRoleEnum;
import com.example.backend.user.repository.RefreshTokenRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "google login")
@Service
@RequiredArgsConstructor
public class GoogleService {
	@Value("${oauth2.google.client-id}")
	private String googleClientId;

	@Value("${oauth2.google.client-secret}")
	private String googleClientPw;

	@Value("${oauth2.google.redirect-uri}")
	private String googleRedirectUri;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RestTemplate restTemplate;
	private final JwtUtil jwtUtil;

	@Transactional
	public TokenDto googleLogin(String code) throws JsonProcessingException {
		log.info("\"인가 코드\"로 \"액세스 토큰\" 요청");
		String accessToken = getGoogleToken(code);
		log.info("토큰으로 구글 API 호출 : \"액세스 토큰\"으로 \"구글 사용자 정보\" 가져오기");
		UserInfoDto googleUserInfo = getUserInfo(accessToken);
		log.info("필요시에 회원가입");
		User googleUser = signupWithGoogleEmail(googleUserInfo);
		log.info("JWT 토큰 반환 시작");
		String createAccessToken = jwtUtil.createAccessToken(googleUser.getEmail(), googleUser.getUserId(),
			googleUser.getNickname(), googleUser.getRole());
		String createRefreshToken = jwtUtil.createRefreshToken(googleUser.getEmail());
		TokenDto tokenDto = new TokenDto(createAccessToken, createRefreshToken,googleUser);
		RefreshToken CheckRefreshToken = refreshTokenRepository.findByKeyEmail(googleUser.getEmail()).orElse(null);
		//해당 email에 대한 refresh 토큰이 있으면 삭제 후 저장.
		if (CheckRefreshToken != null) {
			refreshTokenRepository.delete(CheckRefreshToken);
		}
		RefreshToken newRefreshToken = new RefreshToken(
			jwtUtil.encryptRefreshToken(jwtUtil.substringToken(createRefreshToken)), googleUser.getEmail());
		refreshTokenRepository.save(newRefreshToken);
		log.info("JWT 토큰 반환 종료");
		return tokenDto;
	}

	private String getGoogleToken(String code) throws JsonProcessingException {
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
			.fromUriString("https://oauth2.googleapis.com")
			.path("/token")
			.encode()
			.build()
			.toUri();

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", googleClientId);
		body.add("client_secret", googleClientPw);
		body.add("redirect_uri", googleRedirectUri);
		body.add("code", code);

		RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
			.post(uri)
			.headers(headers)
			.body(body);

		// HTTP 요청 보내기
		ResponseEntity<String> response = restTemplate.exchange(
			requestEntity,
			String.class
		);

		// HTTP 응답 (JSON) -> 액세스 토큰 파싱
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		return jsonNode.get("access_token").asText();
	}

	private UserInfoDto getUserInfo(String accessToken) throws JsonProcessingException {
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
			.fromUriString("https://oauth2.googleapis.com")
			.path("/tokeninfo")
			.encode()
			.build()
			.toUri();

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
			.post(uri)
			.headers(headers)
			.body(new LinkedMultiValueMap<>());

		// HTTP 요청 보내기
		ResponseEntity<String> response = restTemplate.exchange(
			requestEntity,
			String.class
		);

		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		log.info(jsonNode.toString());
		Long googleId = Long.parseLong(jsonNode.get("sub").asText().substring(0, 13));
		String email = jsonNode.get("email").asText();
		String nickname = email.substring(0, email.indexOf('@'));

		return new UserInfoDto(googleId, nickname, email);
	}

	private User signupWithGoogleEmail(UserInfoDto UserInfo) {
		// DB 에 중복된 google Id 가 있는지 확인
		Long googleId = UserInfo.getId();
		User googleUser = userRepository.findByGoogleId(googleId).orElse(null);

		if (googleUser == null) {
			// 구글 사용자 email 동일한 email 가진 회원이 있는지 확인
			String googleEmail = UserInfo.getEmail();
			User sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);
			if (sameEmailUser != null) {
				googleUser = sameEmailUser;
				// 기존 회원정보에 구글 Id 추가
				googleUser = googleUser.googleIdUpdate(googleId);
			} else {
				// 신규 회원가입
				// password: random UUID
				String password = UUID.randomUUID().toString(); //일반 로그인으로 로그인할 수 없도록 설정
				String encodedPassword = passwordEncoder.encode(password);

				// email: google email
				String email = UserInfo.getEmail();

				googleUser = new User(email, encodedPassword, UserInfo.getNickname(), UserRoleEnum.USER, googleId,
					null);
			}

			userRepository.save(googleUser);
		}
		return googleUser;
	}

	public String getGoogleLoginForm() {
		log.info("구글 로그인 페이지 불러오기 시작");
		return "https://accounts.google.com/o/oauth2/v2/auth?client_id="
			+ googleClientId
			+ "&redirect_uri="
			+ googleRedirectUri
			+ "&response_type=code"
			+ "&scope=https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile"
			+ "&access_type=offline";
	}
}
