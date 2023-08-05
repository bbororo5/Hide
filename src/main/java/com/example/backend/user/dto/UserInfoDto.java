package com.example.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {
	private Long id;
	private String nickname;
	private String email;
	private String password;

	public UserInfoDto(Long id, String nickname, String email) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
	}
}
