package com.example.backend.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String refreshToken;

	@Column(nullable = false)
	private String keyEmail;

	public RefreshToken(String refreshToken, String keyEmail) {
		this.refreshToken = refreshToken;
		this.keyEmail = keyEmail;
	}
}
