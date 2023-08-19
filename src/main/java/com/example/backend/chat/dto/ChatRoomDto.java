package com.example.backend.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.chat.entity.ChatRoom;
import com.example.backend.user.entity.User;

import lombok.Getter;

@Getter
public class ChatRoomDto {
	private String roomName;
	private String oppositeUserImage;
	private String oppositeNickname;
	private LocalDateTime modifiedAt;
	private MessageDto lastChatMessage;

	public ChatRoomDto(ChatRoom chatRoom, User oppositeUser) {
		this.roomName = chatRoom.getRoomName();
		if (oppositeUser.getImage() != null) {
			this.oppositeUserImage = oppositeUser.getImage().getImageUrl();
		}
		this.modifiedAt = chatRoom.getModifiedAt();
		this.oppositeNickname = oppositeUser.getNickname();
		List<MessageDto> messages = chatRoom.getChatMessage().stream().map(MessageDto::new)
			.toList();
		if (!messages.isEmpty()) {
			this.lastChatMessage = messages.get(messages.size() - 1);
		}
	}
}
