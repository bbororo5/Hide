package com.example.backend.track.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarListResponseDto {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Double star;

    public StarListResponseDto(Long userId, String nickname, String profileImageUrl, Double star) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.star = star;
    }
}
