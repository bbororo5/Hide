package com.example.backend.user.controller;

import com.example.backend.StatusResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.service.KaKaoService;
import com.example.backend.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RestController
@RequiredArgsConstructor
public class KaKaoController {

	private final KaKaoService kaKaoService;
	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/kakao")
	public String getLoginUrl() {
		return kaKaoService.getKakaoLoginForm();
	}

	//카카오 로그인
	@GetMapping("/api/users/oauth2/kakao")
	public ResponseEntity<StatusResponseDto> kakaoLogin(@RequestParam (value = "code") String code, HttpServletResponse response) throws JsonProcessingException {
		String token = kaKaoService.kakaoLogin(code);
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
		return new ResponseEntity<>(new StatusResponseDto("카카오 로그인 완료되었습니다."), HttpStatus.OK);
	}
}
