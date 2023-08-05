package com.example.backend.spotify.controller;

import com.example.backend.spotify.dto.Track;
import com.example.backend.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @PostMapping("/token")
    public ResponseEntity<?> requestAccessToken() {
        spotifyService.requestAccessToken();
        return ResponseEntity.ok().body("스포티파이에 액세스 토큰 요청 완료");
    }

    @GetMapping("/popular")
    public ResponseEntity<?> requestPopularMusic() {
        List<Track> trackList = spotifyService.getPopularMusics();
        return ResponseEntity.ok().body(trackList);
    }
}
