package com.example.backend.track.entity;

import com.example.backend.user.entity.User;
import jakarta.persistence.*;

import lombok.Getter;


@Entity
@Getter
public class TrackCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "play_count")
    private int playCount = 0;

    public TrackCount(String trackId, User user, int playCount) {
        this.trackId = trackId;
        this.user = user;
        this.playCount = playCount;
    }

    public TrackCount() {
    }

    public void increasePlayCount() {
        this.playCount += 1;
    }


}