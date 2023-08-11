package com.example.backend.chat.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.example.backend.util.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final UserRepository userRepository;
	@Transactional
	public void saveMessages(Long receiverId, MessageDto message) {
		User receiver = userRepository.findById(receiverId)
			.orElseThrow(() -> new NullPointerException("회원이 존재하지 않습니다."));
		User sender = userRepository.findById(message.getSenderId())
			.orElseThrow(() -> new NullPointerException("회원이 존재하지 않습니다."));
		String roomName;
		if(receiverId>message.getSenderId()){
			roomName =message.getSenderId()+ "-" + receiverId;
		}else{
			roomName = receiverId + "-" +message.getSenderId();
		}

		ChatRoom chatRoom = chatRoomRepository.findByRoomName(roomName)
			.orElseGet(() -> {
				ChatRoom newRoom = new ChatRoom(roomName, receiver, sender);
				return chatRoomRepository.save(newRoom);
			});

		ChatMessage newChatMessage = new ChatMessage(message);
		chatRoom.addMessage(newChatMessage);

		chatMessageRepository.save(newChatMessage);
	}

	public ChatResponse getAllMessages(String roomName , UserDetailsImpl userDetails) {
		ChatRoom chatRoom = chatRoomRepository.findByRoomName(roomName)
			.orElseThrow(() -> new NullPointerException("채팅방이 존재하지 않습니다."));
		User sender = chatRoom.getSender();
		User receiver = chatRoom.getReceiver();

		ChatResponse chatResponse = new ChatResponse();
		chatResponse.changeToMessageDto(chatRoom.getChatMessage());
		if(userDetails.getUser().getUserId()==sender.getUserId()){
			chatResponse.setNickname(receiver.getNickname());
		}else{
			chatResponse.setNickname(sender.getNickname());
		}
		return chatResponse;
	}

	public List<ChatRoomDto> getAllRooms(Long userId) {  //이러면 다른사람 채팅방 목록도 볼 수 있음 토큰에서 가져와야할듯
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NullPointerException("회원이 존재하지 않습니다."));
		List<ChatRoom> received = user.getReceivedChatRooms();
		List<ChatRoom> sent = user.getSentChatRooms(); // 이 부분을 올바르게 변경
		return Stream.concat(received.stream(), sent.stream())
			.sorted(Comparator.comparing(ChatRoom::getModifiedAt).reversed()).map(ChatRoomDto::new)
			.toList();
	}
}
