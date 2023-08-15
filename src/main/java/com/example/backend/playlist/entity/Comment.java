package com.example.backend.playlist.entity;

import com.example.backend.playlist.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "star", nullable = false)
    private String star;

    public Comment(String nickname, String content, String userId) {
        this.nickname = nickname;
        this.content = content;
        this.userId = userId;
        this.star = star;
    }
}
