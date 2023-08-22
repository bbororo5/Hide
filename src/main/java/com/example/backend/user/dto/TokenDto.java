package com.example.backend.user.dto;

import com.example.backend.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDto {
	String refreshToken;
	String accessToken;
	User user;

	public TokenDto(String accessToken, String refreshToken , User user) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.user = user;
	}
}
