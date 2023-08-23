package com.example.backend.track.controller;

import java.util.List;

import com.example.backend.track.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.util.StatusResponseDto;
import com.example.backend.track.service.TrackService;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.SpotifyUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
	public ResponseEntity<String> increasePlayCount(@PathVariable(name = "track-id") String trackId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		trackService.increasePlayCount(trackId);
		return ResponseEntity.ok().body("Play count가 1 올랐습니다.");
	}

	@GetMapping("/recommend")
	public ResponseEntity<List<Track>> recommendTracks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(trackService.recommendTracks(userDetails));
	}

	@GetMapping("{track-id}/modal")
	public ResponseEntity<TrackDetailModal> getTrackDetailModal(@PathVariable(name = "track-id") String trackId) {
		return ResponseEntity.ok(trackService.getTrackDetailModal(trackId));
	}

	@GetMapping("{track-id}")
	public ResponseEntity<TrackDetailDto> getTrackDetail(@PathVariable(name = "track-id") String trackId) {
		return ResponseEntity.ok(trackService.getTrackDetail(trackId));
	}

	@GetMapping("/search")
	public ResponseEntity<List<Track>> trackSearch(@RequestParam String keyword) {
		return ResponseEntity.ok(spotifyUtil.getSearchResult(keyword));
	}

	@GetMapping("/users/{user-id}/recent")
	public ResponseEntity<List<Track>> getRecentTracks(@PathVariable(name = "user-id") Long userId) {
		return ResponseEntity.ok(trackService.getRecentTracks(userId));
	}

	@PostMapping("/{track-id}/recent")
	public ResponseEntity<String> postRecentTrack(@PathVariable(name = "track-id") String trackId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		trackService.createRecentTrack(trackId, userDetails.getUser());
		return ResponseEntity.ok("최근 들은 목록에 트랙이 추가되었습니다.");
	}

	@GetMapping("/search/recommend-keywords")
	public ResponseEntity<List<Top7Dto>> get7RecentTracks() {
		return ResponseEntity.ok(trackService.get7RecentTracks());
	}

	@PutMapping("/{track-id}/star")
	public ResponseEntity<StatusResponseDto> setStarRating(@PathVariable(name = "track-id") String trackId,
		@Valid @RequestBody StarDto starDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return trackService.setStarRating(trackId, starDto, userDetails);
	}

	@DeleteMapping("/{track-id}/star")
	public ResponseEntity<StatusResponseDto> deleteStarRating(@PathVariable(name = "track-id") String trackId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return trackService.deleteStarRating(trackId, userDetails);
	}

	@GetMapping("/{track-id}/starList")
	public ResponseEntity<List<StarListResponseDto>> getStarList(@PathVariable(name = "track-id") String trackId) {

		return trackService.getStarList(trackId);
	}

}
