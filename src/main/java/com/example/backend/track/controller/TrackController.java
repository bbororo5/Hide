package com.example.backend.track.controller;

import com.example.backend.track.dto.TrackDetailModal;
import com.example.backend.track.service.TrackService;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.SpotifyUtil;
import com.example.backend.track.dto.Track;

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
    private final SpotifyUtil spotifyUtil;

    @GetMapping("/popular")
    public ResponseEntity<List<Track>> getTopTracks() {
        return ResponseEntity.ok(trackService.getTopTracksByAllUser());
    }

    @PatchMapping("/play-count/{track-id}")
    public ResponseEntity<String> increasePlayCount(@PathVariable(name = "track-id") String trackId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        trackService.increasePlayCount(trackId, userDetails.getUser());
        return ResponseEntity.ok().body("Play count가 1 올랐습니다.");
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<Track>> recommendTracks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(trackService.recommendTracks(userDetails));
    }

    @GetMapping("{track-id}/modal")
    public ResponseEntity<TrackDetailModal> getTrackDetail(@PathVariable(name = "track-id") String trackId) {
        return ResponseEntity.ok(trackService.getTrackDetail(trackId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Track>> trackSearch(@RequestParam String keyword) {
        return ResponseEntity.ok(spotifyUtil.getSearchResult(keyword));
    }

    @GetMapping("/{user-id}/recent")
    public ResponseEntity<List<Track>> getRecentTracks(@PathVariable(name = "user-id") Long userId) {
        return ResponseEntity.ok(trackService.getRecentTracks(userId));
    }

    @PostMapping("/{track-id}/recent")
    public ResponseEntity<String> postRecentTrack(@PathVariable(name = "track-id") String trackId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        trackService.createRecentTrack(trackId, userDetails.getUser());
        return  ResponseEntity.ok("최근 들은 목록에 트랙이 추가되었습니다.");
    }


}
