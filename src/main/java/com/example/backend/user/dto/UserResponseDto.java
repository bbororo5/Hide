package com.example.backend.user.dto;

import com.example.backend.user.entity.User;

import lombok.Getter;

@Getter
public class UserResponseDto {
	private Long id;
	private String nickname;

	public UserResponseDto(User user){
		this.id=user.getUserId();
		this.nickname=user.getNickname();
	}
}
