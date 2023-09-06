package com.example.backend.track.dto;

import java.time.LocalDateTime;

import com.example.backend.track.entity.Comment;

import lombok.Getter;

@Getter
public class CommentResponseDto {

	private Long id;
	private Long userId;
	private String nickname;
	private String content;
	private String imageUrl;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public CommentResponseDto(Comment comment) {
		this.userId = comment.getUser().getUserId();
		this.id = comment.getId();
		if (comment.getUser().getImage() != null) {
			this.imageUrl = comment.getUser().getImage().getImageUrl();
		}
		this.nickname = comment.getUser().getNickname();
		this.content = comment.getContent();
		this.createdAt = comment.getCreatedAt();
		this.modifiedAt = comment.getModifiedAt();
	}
}
