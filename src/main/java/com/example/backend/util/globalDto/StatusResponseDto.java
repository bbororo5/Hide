package com.example.backend.util.globalDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
	private String msg;
	private boolean isSuccess;

	public StatusResponseDto(String msg) {
		this.msg = msg;
	}

	public StatusResponseDto(String msg, boolean isSuccess) {
		this.msg = msg;
		this.isSuccess = isSuccess;
	}
}
