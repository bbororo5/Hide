package com.example.backend.chat.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.chat.dto.ChatResponse;
import com.example.backend.chat.dto.ChatRoomDto;
import com.example.backend.chat.dto.MessageDto;
import com.example.backend.chat.entity.ChatMessage;
import com.example.backend.chat.entity.ChatRoom;
import com.example.backend.chat.repository.ChatMessageRepository;
import com.example.backend.chat.repository.ChatRoomRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final UserRepository userRepository;

	@Transactional
	public void saveMessages(Long receiverId, MessageDto message) {
		log.info("채팅 메세지 저장 시작");
		User receiver = userRepository.findById(receiverId)
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
		User sender = userRepository.findById(message.getSenderId())
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
		String roomName;
		if (receiverId > message.getSenderId()) {
			roomName = message.getSenderId() + "-" + receiverId;
		} else {
			roomName = receiverId + "-" + message.getSenderId();
		}

		ChatRoom chatRoom = chatRoomRepository.findByRoomName(roomName)
			.orElseGet(() -> {
				ChatRoom newRoom = new ChatRoom(roomName, receiver, sender);
				return chatRoomRepository.save(newRoom);
			});

		ChatMessage newChatMessage = new ChatMessage(message);
		chatRoom.addMessage(newChatMessage);

		chatMessageRepository.save(newChatMessage);
		log.info("채팅 메세지 저장 완료");
	}

	@Transactional(readOnly = true)
	public ChatResponse getAllMessages(String roomName, UserDetailsImpl userDetails) {
		log.info("채팅방 별 채팅 목록 불러오기 시작");
		ChatRoom chatRoom = chatRoomRepository.findByRoomName(roomName)
			.orElseThrow(() -> new NoSuchElementException("채팅방이 존재하지 않습니다."));
		User sender = chatRoom.getSender();
		User receiver = chatRoom.getReceiver();

		ChatResponse chatResponse = new ChatResponse();
		chatResponse.changeToMessageDto(chatRoom.getChatMessage());
		if (userDetails.getUser().getUserId() == sender.getUserId()) {
			chatResponse.setNickname(receiver.getNickname());
		} else {
			chatResponse.setNickname(sender.getNickname());
		}
		log.info("채팅방 별 채팅 목록 불러오기 완료");
		return chatResponse;
	}

	@Transactional(readOnly = true)
	public List<ChatRoomDto> getAllRooms(Long userId) {
		log.info("유저의 채팅방 목록 불러오기 시작");
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
		List<ChatRoom> received = user.getReceivedChatRooms();
		List<ChatRoom> sent = user.getSentChatRooms(); // 이 부분을 올바르게 변경
		List<ChatRoom> combined = new ArrayList<>();
		combined.addAll(received);
		combined.addAll(sent);

		combined.sort(Comparator.comparing(ChatRoom::getModifiedAt).reversed());

		List<ChatRoomDto> result = new ArrayList<>();

		for (ChatRoom chatRoom : combined) {
			User oppositeUser;
			if (userId == chatRoom.getSender().getUserId()) {
				oppositeUser = chatRoom.getReceiver();
			} else {
				oppositeUser = chatRoom.getSender();
			}
			ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom, oppositeUser); // 필요한 경우 파라미터 추가
			result.add(chatRoomDto);
		}
		log.info("유저의 채팅방 목록 불러오기 완료");
		return result;
	}
}
