package com.example.backend.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

	private Long senderId;
	private String nickname;
	private String message;
	private String sendDate;
	private String sendTime;
}
