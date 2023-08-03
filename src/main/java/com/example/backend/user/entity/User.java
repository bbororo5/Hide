package com.example.backend.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false, unique = true)
	private String nickname;
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UserRoleEnum role;
	private Long kakaoId;
	private Long googleId;

	public User(String email, String password, String nickname, UserRoleEnum role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
	}
	public User(String email, String password, String nickname, UserRoleEnum role, Long googleId ,Long kakaoId) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.googleId =googleId;
		this.kakaoId = kakaoId;
	}

	public User kakaoIdUpdate(Long kakaoId) {
		this.kakaoId = kakaoId;
		return this;
	}
	public User googleIdUpdate(Long googleId) {
		this.googleId = googleId;
		return this;
	}

}
