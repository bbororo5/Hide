package com.example.backend.chat.dto;

import java.util.List;

import com.example.backend.chat.entity.ChatMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponse {
	private String nickname;
	private List<MessageDto> messages;

	public void changeToMessageDto(List<ChatMessage> messages) {
		this.messages = messages.stream().map(MessageDto::new)
			.toList();
	}
}
