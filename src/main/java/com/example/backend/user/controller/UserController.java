package com.example.backend.user.controller;

import com.example.backend.StatusResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.security.UserDetailsImpl;
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
	public ResponseEntity<StatusResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
		return userService.signup(signupRequestDto);
	}

	@DeleteMapping("")
	public ResponseEntity<StatusResponseDto> removeUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return userService.removeUser(userDetails);
	}

	@PostMapping("/follow/{userId}")
	public ResponseEntity<StatusResponseDto> followUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return userService.followUser(userId ,userDetails);
	}
}
