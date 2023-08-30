package com.example.backend.user.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.TokenDto;
import com.example.backend.user.dto.UserResponseDto;
import com.example.backend.user.service.KaKaoService;
import com.example.backend.util.ImageUtil;
import com.example.backend.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KaKaoController {

	private final KaKaoService kaKaoService;
	private final ImageUtil imageUtil;

	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/kakao")
	public void getLoginUrl(HttpServletResponse response) throws IOException {
		log.info("카카오 로그인 페이지 불러오기");
		String url = kaKaoService.getKakaoLoginForm();
		response.sendRedirect(url);
	}

	//카카오 로그인
	@GetMapping("/api/users/oauth2/kakao")
	public void kakaoLogin(@RequestParam(value = "code") String code,
		HttpServletResponse response) throws
		IOException {
		log.info("카카오 로그인 요청");
		TokenDto tokenDto = kaKaoService.kakaoLogin(code);
		// 액세스 토큰을 쿠키로 설정
		ResponseCookie accessTokenCookie = ResponseCookie.from(JwtUtil.AUTHORIZATION_HEADER, tokenDto.getAccessToken())
			.secure(true)    // Secure 설정
			.path("/")       // Path 설정
			.sameSite("None") // SameSite 설정
			.build();

		// 리프레시 토큰도 쿠키로 설정
		ResponseCookie refreshTokenCookie = ResponseCookie.from(JwtUtil.REFRESH_HEADER, tokenDto.getRefreshToken())
			.secure(true)    // Secure 설정
			.path("/")       // Path 설정
			.sameSite("None") // SameSite 설정
			.build();

		// 쿠키 추가
		response.addHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());
		response.sendRedirect("https://hide-iota.vercel.app");
	}
}
