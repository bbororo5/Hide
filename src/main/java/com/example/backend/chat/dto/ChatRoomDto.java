package com.example.backend.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.chat.entity.ChatMessage;
import com.example.backend.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomDto {
	private String roomName;
	private LocalDateTime modifiedAt;
	private ChatMessage lastChatMessage;

	public ChatRoomDto(ChatRoom chatRoom) {
		this.roomName = chatRoom.getRoomName();
		this.modifiedAt = chatRoom.getModifiedAt();
		List<ChatMessage> messages = chatRoom.getChatMessage();
		if(!messages.isEmpty()){
			this.lastChatMessage = messages.get(messages.size()-1);
		}
	}
}
