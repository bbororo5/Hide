package com.example.backend.playlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    private String content;
    private String nickname;
    private String star;

    public CommentRequestDto(String content, String nickname, String star) {
        this.content = content;
        this.nickname = nickname;
        this.star = star;
    }

    public String getContent() {
        return content;
    }

    public String getNickname() {
        return nickname;
    }

    public String getStar() {
        return star;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setStar(String star) {
        this.star = star;
    }
}
