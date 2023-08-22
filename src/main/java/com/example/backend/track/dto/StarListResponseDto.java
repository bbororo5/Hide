package com.example.backend.track.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarListResponseDto {

    private String userId;
    private String nickname;
    private String profileImageUrl;
    private Long star;

    StarListResponseDto(){

    }
}
