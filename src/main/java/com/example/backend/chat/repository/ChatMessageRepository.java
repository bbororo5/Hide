package com.example.backend.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
