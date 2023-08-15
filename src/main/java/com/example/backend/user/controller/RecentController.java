package com.example.backend.user.controller;

import com.example.backend.util.spotify.dto.Track;
import com.example.backend.util.spotify.SpotifyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecentController {
    private final SpotifyUtil spotifyUtil;

    @GetMapping("/musics/{userId}/lately")
    public ResponseEntity<List<Track>> getRecentTracks(@PathVariable Long userId) {
        return ResponseEntity.ok(spotifyUtil.getRecentTracks(userId));
    }
}
