package com.example.backend.util.spotify.controller;

import com.example.backend.util.spotify.dto.Track;
import com.example.backend.util.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/tracks")
    public ResponseEntity<List<Track>> getTracks(@RequestParam List<String> trackIds) {
        List<Track> tracks = spotifyService.getTracksInfo(trackIds);
        return ResponseEntity.ok(tracks);
    }
}
