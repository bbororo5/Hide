package com.example.backend.user.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.TokenDto;
import com.example.backend.user.service.GoogleService;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.globalDto.StatusResponseDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GoogleController {

	private final GoogleService googleService;

	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/google")
	public void getLoginUrl(HttpServletResponse response) throws IOException {
		log.info("구글 로그인 페이지 불러오기");
		String url = googleService.getGoogleLoginForm();
		response.sendRedirect(url);
	}

	// 구글 로그인
	@GetMapping("/api/users/oauth2/google")
	public ResponseEntity<StatusResponseDto> googleLogin(@RequestParam String code, HttpServletResponse response) throws
		IOException {
		log.info("구글 로그인 요청");
		TokenDto tokenDto = googleService.googleLogin(code);
		String accessToken = tokenDto.getAccessToken().substring(7);
		String refreshToken = tokenDto.getRefreshToken().substring(7);

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
		response.addHeader(JwtUtil.REFRESH_HEADER, refreshToken);
		return new ResponseEntity<>(new StatusResponseDto("구글 로그인 완료.", true), HttpStatus.OK);
	}
}
