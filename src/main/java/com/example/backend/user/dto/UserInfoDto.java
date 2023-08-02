package com.example.backend.user.dto;

import lombok.Getter;

@Getter
public class UserInfoDto {
	private Long id;
	private String nickname;
	private String email;

	public UserInfoDto(Long id, String nickname, String email) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
	}
}
