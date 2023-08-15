package com.example.backend.playlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    private String content;
    private Double star;

    public CommentRequestDto(String content, Double star) {
        this.content = content;
        this.star = star;
    }

    public String getContent() {
        return content;
    }

    public Double getStar() {
        return star;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStar(Double star) {
        this.star = star;
    }
}
