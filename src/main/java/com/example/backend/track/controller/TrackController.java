package com.example.backend.track.controller;

import com.example.backend.track.service.TrackService;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.dto.Track;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    @GetMapping("/popular")
    public ResponseEntity<List<Track>> getTopTracks() {
        return ResponseEntity.ok(trackService.getTopTracks());
    }

    @PatchMapping("/play-count")
    public ResponseEntity<?> increasePlayCount(String trackId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        trackService.increasePlayCount(trackId, userDetails.getUser());
        return ResponseEntity.ok().body("Play count가 1 올랐습니다.");
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> recommendTracks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(trackService.recommendTracks(userDetails));
    }
}
