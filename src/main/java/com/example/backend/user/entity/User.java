package com.example.backend.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.backend.chat.entity.ChatRoom;
import com.example.backend.playlist.entity.Playlist;
import com.example.backend.track.entity.TrackCount;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false, unique = true)
	private String nickname;
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UserRoleEnum role;
	@Column
	private Long kakaoId;
	@Column
	private Long googleId;

	@OneToMany(mappedBy = "user")
	private List<TrackCount> trackCounts = new ArrayList<>();

	@OneToMany(mappedBy = "toUser")
	private List<Follow> followingList = new ArrayList<>();
	@OneToMany(mappedBy = "fromUser")
	private List<Follow> followerList = new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "image_key")
	private Image image;

	@OneToMany(mappedBy = "sender")
	@JsonManagedReference
	private List<ChatRoom> sentChatRooms = new ArrayList<>();

	@OneToMany(mappedBy = "receiver")
	@JsonManagedReference
	private List<ChatRoom> receivedChatRooms = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Playlist> playlists = new ArrayList<>();

	public void updateUserImage(Image image) {
		this.image = image;
	}
	public void updateUserNickname(String nickname) {
		this.nickname = nickname;
	}

	public User(String email, String password, String nickname, UserRoleEnum role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
	}

	public User(String email, String password, String nickname, UserRoleEnum role, Long googleId, Long kakaoId) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.googleId = googleId;
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

	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}
}
