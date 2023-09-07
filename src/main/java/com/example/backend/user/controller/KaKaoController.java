package com.example.backend.user.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.TokenDto;
import com.example.backend.user.service.KaKaoService;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.globalDto.StatusResponseDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KaKaoController {

	private final KaKaoService kaKaoService;

	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/kakao")
	public void getLoginUrl(HttpServletResponse response) throws IOException {
		log.info("카카오 로그인 페이지 불러오기");
		String url = kaKaoService.getKakaoLoginForm();
		response.sendRedirect(url);
	}

	//카카오 로그인
	@GetMapping("/api/users/oauth2/kakao")
	public ResponseEntity<StatusResponseDto> kakaoLogin(@RequestParam(value = "code") String code,
		HttpServletResponse response) throws
		IOException {
		log.info("카카오 로그인 요청");
		TokenDto tokenDto = kaKaoService.kakaoLogin(code);
		String accessToken = tokenDto.getAccessToken().substring(7);
		String refreshToken = tokenDto.getRefreshToken().substring(7);

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
		response.addHeader(JwtUtil.REFRESH_HEADER, refreshToken);
		return new ResponseEntity<>(new StatusResponseDto("카카오 로그인 완료.", true), HttpStatus.OK);
	}
}
