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
	public UserProfileDto(User user) {
		this.userId = user.getUserId();
		this.nickname = user.getNickname();
		if(user.getImage()!=null){
			this.imageUrl = user.getImage().getImageUrl();
		}
		this.following = user.getFollowerList().size();
		this.follower = user.getFollowingList().size();
	}
}
