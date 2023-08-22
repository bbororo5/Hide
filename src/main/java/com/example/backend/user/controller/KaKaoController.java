package com.example.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.TokenDto;
import com.example.backend.user.dto.UserResponseDto;
import com.example.backend.user.service.KaKaoService;
import com.example.backend.util.ImageUtil;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.StatusResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KaKaoController {

	private final KaKaoService kaKaoService;
	private final ImageUtil imageUtil;

	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/kakao")
	public ResponseEntity<StatusResponseDto> getLoginUrl() {
		String url = kaKaoService.getKakaoLoginForm();
		return new ResponseEntity<>(new StatusResponseDto(url, true), HttpStatus.OK);
	}

	//카카오 로그인
	@GetMapping("/api/users/oauth2/kakao")
	public ResponseEntity<UserResponseDto> kakaoLogin(@RequestParam(value = "code") String code,
		HttpServletResponse response) throws
		JsonProcessingException {
		TokenDto tokenDto = kaKaoService.kakaoLogin(code);
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, tokenDto.getAccessToken());
		response.addHeader(JwtUtil.REFRESH_HEADER, tokenDto.getRefreshToken());
		return new ResponseEntity<>(
			new UserResponseDto("카카오 로그인이 완료되었습니다.", true, imageUtil.getImageUrlFromUser(tokenDto.getUser())),
			HttpStatus.OK);
	}
}
