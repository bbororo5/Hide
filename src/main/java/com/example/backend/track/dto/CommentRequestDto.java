package com.example.backend.track.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
	private String content;

	public CommentRequestDto(String content, Double star) {
		this.content = content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
