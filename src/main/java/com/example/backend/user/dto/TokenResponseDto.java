package com.example.backend.user.dto;

import lombok.Getter;

@Getter
public class TokenResponseDto {
	private String msg;
	private boolean isExpired;

	public TokenResponseDto(String msg) {
		this.msg = msg;
	}

	public TokenResponseDto(String msg, boolean isExpired) {
		this.msg = msg;
		this.isExpired = isExpired;
	}
}
