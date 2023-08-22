package com.example.backend.user.dto;

import com.example.backend.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileDto {
	private Long userId;
	private String nickname;
	private String imageUrl;
	private int following;
	private int follower;
	private boolean isFollowing;
	public boolean getIsFollowing() {
		return isFollowing;
	}

	public UserProfileDto(User user,boolean isFollowing) {
		this.userId = user.getUserId();
		this.nickname = user.getNickname();
		if (user.getImage() != null) {
			this.imageUrl = user.getImage().getImageUrl();
		}
		this.following = user.getFollowingList().size();
		this.follower = user.getFollowerList().size();
		this.isFollowing = isFollowing;
	}
}
