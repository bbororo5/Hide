package com.example.backend.track.entity;

import com.example.backend.track.dto.CommentRequestDto;
import com.example.backend.user.entity.User;
import com.example.backend.util.Timestamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "content", nullable = false)
	private String content;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	private String trackId;

	public Comment(String content, User user, String trackId) {
		this.content = content;
		this.user = user;
		this.trackId = trackId;
	}

	public void updateComment(CommentRequestDto requestDto) {
		this.content = requestDto.getContent();
	}
}
