package com.example.backend.user.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.backend.user.dto.TokenDto;
import com.example.backend.user.service.KaKaoService;
import com.example.backend.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KaKaoController {

	private final KaKaoService kaKaoService;

	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/kakao")
	public void getLoginUrl(HttpServletResponse response) throws IOException {
		String url = kaKaoService.getKakaoLoginForm();
		response.sendRedirect(url);
	}

	//카카오 로그인
	@GetMapping("/api/users/oauth2/kakao")
	public RedirectView kakaoLogin(@RequestParam(value = "code") String code, HttpServletResponse response) throws
		JsonProcessingException {
		TokenDto tokenDto = kaKaoService.kakaoLogin(code);
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, tokenDto.getAccessToken());
		response.addHeader(JwtUtil.REFRESH_HEADER, tokenDto.getRefreshToken());
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("https://hide-iota.vercel.app");
		return redirectView;
	}
}
