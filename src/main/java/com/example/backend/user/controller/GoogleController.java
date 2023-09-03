package com.example.backend.user.controller;

import java.io.IOException;

import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.TokenDto;
import com.example.backend.user.service.GoogleService;
import com.example.backend.util.ImageUtil;
import com.example.backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GoogleController {

	private final GoogleService googleService;
	private final ImageUtil imageUtil;

	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/google")
	public void getLoginUrl(HttpServletResponse response) throws IOException {
		log.info("구글 로그인 페이지 불러오기");
		String url = googleService.getGoogleLoginForm();
		response.sendRedirect(url);
	}

	// 구글 로그인
	@GetMapping("/api/users/oauth2/google")
	public void googleLogin(@RequestParam String code, HttpServletResponse response) throws
		IOException {
		log.info("구글 로그인 요청");
		TokenDto tokenDto = googleService.googleLogin(code);
		String accessToken = tokenDto.getAccessToken().substring(7);
		String refreshToken = tokenDto.getRefreshToken().substring(7);
		// 액세스 토큰을 쿠키로 설정
		ResponseCookie accessTokenCookie = ResponseCookie.from(JwtUtil.AUTHORIZATION_HEADER, accessToken)
			.secure(true)    // Secure 설정
			.httpOnly(true)
			.path("/")       // Path 설정
			.sameSite("None") // SameSite 설정
			.build();

		// 리프레시 토큰도 쿠키로 설정
		ResponseCookie refreshTokenCookie = ResponseCookie.from(JwtUtil.REFRESH_HEADER, refreshToken)
			.secure(true)    // Secure 설정
			.httpOnly(true)
			.path("/")       // Path 설정
			.sameSite("None") // SameSite 설정
			.build();

		// 쿠키 추가
		response.addHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		response.sendRedirect("https://hide-iota.vercel.app");
	}
}
