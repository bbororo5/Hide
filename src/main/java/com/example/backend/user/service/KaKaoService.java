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

@Slf4j(topic = "Kakao login")
@Service
@RequiredArgsConstructor
public class KaKaoService {
	@Value("${oauth2.kakao.client-id}")
	private String kakaoClientId;
	@Value("${oauth2.kakao.client-secret}")
	private String kakaoClientPw;
	@Value("${oauth2.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RestTemplate restTemplate;
	private final JwtUtil jwtUtil;

	@Transactional
	public TokenDto kakaoLogin(String code) throws JsonProcessingException {
		log.info("\"인가 코드\"로 \"액세스 토큰\" 요청");
		String accessToken = getKakaoToken(code);
		log.info("토큰으로 카카오 API 호출 : \"액세스 토큰\"으로 \"카카오 사용자 정보\" 가져오기");
		UserInfoDto kakaoUserInfo = getUserInfo(accessToken);
		log.info("필요시에 회원가입");
		User kakaoUser = signupWithKaKaoEmail(kakaoUserInfo);
		log.info("JWT 토큰 반환 시작");
		String createAccessToken = jwtUtil.createAccessToken(kakaoUser.getEmail(), kakaoUser.getUserId(),
			kakaoUser.getNickname(), kakaoUser.getRole());
		String createRefreshToken = jwtUtil.createRefreshToken(kakaoUser.getEmail());
		TokenDto tokenDto = new TokenDto(createAccessToken, createRefreshToken, kakaoUser);
		RefreshToken CheckRefreshToken = refreshTokenRepository.findByKeyEmail(kakaoUser.getEmail()).orElse(null);
		//해당 email에 대한 refresh 토큰이 있으면 삭제 후 저장.
		if (CheckRefreshToken != null) {
			refreshTokenRepository.delete(CheckRefreshToken);
		}
		RefreshToken newRefreshToken = new RefreshToken(
			jwtUtil.encryptRefreshToken(jwtUtil.substringToken(createRefreshToken)), kakaoUser.getEmail());
		refreshTokenRepository.save(newRefreshToken);
		log.info("JWT 토큰 반환 종료");
		return tokenDto;
	}

	private String getKakaoToken(String code) throws JsonProcessingException {
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
			.fromUriString("https://kauth.kakao.com")
			.path("/oauth/token")
			.encode()
			.build()
			.toUri();

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", kakaoClientId);
		body.add("client_secret", kakaoClientPw);
		body.add("redirect_uri", kakaoRedirectUri);
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
			.fromUriString("https://kapi.kakao.com")
			.path("/v2/user/me")
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
		Long kakaoId = jsonNode.get("id").asLong();
		String nickname = jsonNode.get("properties")
			.get("nickname").asText();
		String email = jsonNode.get("kakao_account")
			.get("email").asText();

		return new UserInfoDto(kakaoId, nickname, email);
	}

	private User signupWithKaKaoEmail(UserInfoDto UserInfo) {
		// DB 에 중복된 Kakao Id 가 있는지 확인
		Long kakaoId = UserInfo.getId();
		User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

		if (kakaoUser == null) {
			// 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
			String kakaoEmail = UserInfo.getEmail();
			User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
			if (sameEmailUser != null) {
				kakaoUser = sameEmailUser;
				// 기존 회원정보에 카카오 Id 추가
				kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
			} else {
				// 신규 회원가입
				// password: random UUID
				String password = UUID.randomUUID().toString(); //일반 로그인으로 로그인할 수 없도록 설정
				String encodedPassword = passwordEncoder.encode(password);

				// email: kakao email
				String email = UserInfo.getEmail();

				kakaoUser = new User(email, encodedPassword, UserInfo.getNickname(), UserRoleEnum.USER, null, kakaoId);
			}

			userRepository.save(kakaoUser);
		}

		return kakaoUser;
	}

	public String getKakaoLoginForm() {
		return "https://kauth.kakao.com/oauth/authorize?client_id="
			+ kakaoClientId
			+ "&redirect_uri="
			+ kakaoRedirectUri
			+ "&response_type=code";
	}
}
