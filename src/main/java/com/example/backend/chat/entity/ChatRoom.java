package com.example.backend.chat.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.util.Timestamped;
import com.example.backend.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long Id;
	@Column
	String roomName;
	@ManyToOne
	@JoinColumn(name = "receiver_id")
	@JsonBackReference // 이 쪽 관계는 JSON에서 제외
	User receiver;
	@ManyToOne
	@JoinColumn(name = "sender_id")
	@JsonBackReference // 이 쪽 관계는 JSON에서 제외
	User sender;

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference // 이 쪽 관계는 JSON에 포함
	List<ChatMessage> chatMessage = new ArrayList<>();

	public void addMessage(ChatMessage chatMessage) {
		this.chatMessage.add(chatMessage);
		chatMessage.setChatRoom(this);
		this.modifiedAt = LocalDateTime.now();
	}

	public ChatRoom(String roomName, User receiver) {
		this.roomName = roomName;
		this.receiver = receiver;
	}

	public ChatRoom(String roomName, User receiver, User sender) {
		this.roomName = roomName;
		this.receiver = receiver;
		this.sender = sender;
	}
}
