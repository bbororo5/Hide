package com.example.backend.playlist.entity;

import com.example.backend.playlist.dto.CommentRequestDto;
import com.example.backend.playlist.entity.Timestamped;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "star", nullable = false)
    private Double star;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String trackId;

    public Comment(String content, User user, Double star, String trackId) {
        this.content = content;
        this.user = user;
        this.star = star;
        this.trackId = trackId;
    }

    public void updateComment(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
        this.star = requestDto.getStar();
    }
}
