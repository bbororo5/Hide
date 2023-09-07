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
import lombok.NoArgsConstructor;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Recent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recent_id")
	private Long recentId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime creationDate;

	@Column(name = "track_id")
	private String trackId;

	public Recent(String trackId, User user) {
		this.trackId = trackId;
		this.user = user;
	}

}
