package com.example.backend.track.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.backend.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class TrackCount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String trackId;

	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "play_count")
	private int playCount = 0;

	public TrackCount(String trackId, int playCount) {
		this.trackId = trackId;
		this.playCount = playCount;
	}

	public TrackCount() {
	}

	public void increasePlayCount() {
		this.playCount += 1;
	}
}