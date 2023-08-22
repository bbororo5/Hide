package com.example.backend.user.dto;

import lombok.Getter;

@Getter
public class UserResponseDto {
	private String msg;
	private boolean isSuccess;
	private String userProfileImage;

	public UserResponseDto(String msg, boolean isSuccess, String userProfileImage) {
		this.msg = msg;
		this.isSuccess = isSuccess;
		this.userProfileImage = userProfileImage;
	}

}
