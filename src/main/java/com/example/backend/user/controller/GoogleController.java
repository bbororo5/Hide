package com.example.backend.user.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.backend.user.service.GoogleService;
import com.example.backend.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GoogleController {
	private final GoogleService googleService;

	// 로그인 페이지 url 얻기
	@GetMapping("/login/oauth2/google")
	public void getLoginUrl(HttpServletResponse response) throws IOException {
		String url = googleService.getGoogleLoginForm();
		response.sendRedirect(url);
	}

	// 구글 로그인
	@GetMapping("/api/users/oauth2/google")
	public RedirectView googleLogin(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		String token = googleService.googleLogin(code);
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("https://front-end-omega-topaz-47.vercel.app");
		return redirectView;
	}
}
