package com.example.backend.user.dto;

import com.example.backend.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {
	private Long id;
	// @Pattern(regexp = "^[가-힣A-Za-z0-9]{4,10}")
	private String nickname;
	private String email;
	// @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*\\])(?=.]{8,15}$")
	private String password;

	public UserInfoDto(Long id, String nickname, String email) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
	}
	public UserInfoDto(User user){
		this.id=user.getUserId();
		this.nickname=user.getNickname();
	}
}
