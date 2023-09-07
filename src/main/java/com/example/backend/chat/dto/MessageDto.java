package com.example.backend.chat.dto;

import java.time.LocalDateTime;

import com.example.backend.chat.entity.ChatMessage;
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
	private String senderImageUrl;
	private LocalDateTime createdAt;

	public MessageDto(ChatMessage chatMessage) {
		this.senderId = chatMessage.getSenderId();
		this.nickname = chatMessage.getNickname();
		this.message = chatMessage.getMessage();
		this.createdAt = chatMessage.getCreatedAt();
		this.senderImageUrl = chatMessage.getSenderImageUrl();
	}

}
