package com.example.backend.chat.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.backend.chat.dto.ChatResponse;
import com.example.backend.chat.dto.ChatRoomDto;
import com.example.backend.chat.dto.MessageDto;
import com.example.backend.chat.service.ChatService;
import com.example.backend.util.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ChatService chatService;

	@MessageMapping("/user/{user-id}")
	public void sendMessage(@DestinationVariable(value = "user-id") Long receiverId, MessageDto message) {
		log.info("채팅 {} 보낸사람 {} 받는사람 {}", message.getMessage(), message.getSenderId(), receiverId);
		chatService.saveMessages(receiverId, message);
		simpMessagingTemplate.convertAndSend("/sub/user/" + receiverId, message);
	}

	@ResponseBody
	@GetMapping("/api/chat/{room-name}/chat-list")
	public ChatResponse getAllMessages(@PathVariable(value = "room-name") String roomName,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		log.info("채팅방 별 채팅 목록 불러오기");
		return chatService.getAllMessages(roomName, userDetails);
	}

	@ResponseBody
	@GetMapping("/api/chat/{user-id}/room-list") //이러면 다른사람 채팅방 목록도 볼 수 있음 토큰에서 가져와야할듯
	public List<ChatRoomDto> getAllRooms(@PathVariable(value = "user-id") Long userId) {
		log.info("유저의 채팅방 목록 불러오기");
		return chatService.getAllRooms(userId);
	}
}
