package com.example.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
		return userService.signup(signupRequestDto);
	}
}
