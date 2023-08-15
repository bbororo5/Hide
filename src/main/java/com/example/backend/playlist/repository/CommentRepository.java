package com.example.backend.playlist.repository;

import com.example.backend.playlist.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
