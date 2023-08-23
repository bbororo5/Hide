package com.example.backend.track.controller;

import java.util.List;

import com.example.backend.track.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.backend.util.spotify.SpotifyRequestManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {
	private static final Logger logger = LoggerFactory.getLogger(TrackController.class);
	private final TrackService trackService;
	private final SpotifyRequestManager spotifyUtil;

	@GetMapping("/popular")
	public ResponseEntity<List<Track>> getTopTracks() {
		logger.info("모든 유저를 바탕으로 추출한 top 트랙 가져오기");
		return ResponseEntity.ok(trackService.getTopTracksByAllUser());
	}

	@PatchMapping("/play-count/{track-id}")
	public ResponseEntity<String> increasePlayCount(@PathVariable(name = "track-id") String trackId,
													@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("재생횟수 증가하기");
		trackService.increasePlayCount(trackId);
		return ResponseEntity.ok().body("Play count가 1 올랐습니다.");
	}

	@GetMapping("/recommend")
	public ResponseEntity<List<Track>> recommendTracks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("추천 트랙 가져오기");
		List<Track> tracks = trackService.recommendTracks(userDetails);
		return ResponseEntity.ok(tracks);
	}

	@GetMapping("{track-id}/modal")
	public ResponseEntity<TrackDetailModal> getTrackDetailModal(@PathVariable(name = "track-id") String trackId) {
		logger.info("모달용 트랙 세부정보 가져오기");
		return ResponseEntity.ok(trackService.getTrackDetailModal(trackId));
	}

	@GetMapping("{track-id}")
	public ResponseEntity<TrackDetailDto> getTrackDetail(@PathVariable(name = "track-id") String trackId) {
		logger.info("트랙 세부정보 가져오기");
		return ResponseEntity.ok(trackService.getTrackDetail(trackId));
	}

	@GetMapping("/search")
	public ResponseEntity<List<Track>> trackSearch(@RequestParam String keyword) {
		logger.info("트랙 검색. 검색 키워드: {}", keyword);
		return ResponseEntity.ok(spotifyUtil.getSearchResult(keyword));
	}

	@GetMapping("/users/{user-id}/recent")
	public ResponseEntity<List<Track>> getRecentTracks(@PathVariable(name = "user-id") Long userId) {
		logger.info("해당 유저의 최근 들은 트랙 가져오기");
		return ResponseEntity.ok(trackService.getRecentTracks(userId));
	}

	@PostMapping("/{track-id}/recent")
	public ResponseEntity<String> postRecentTrack(@PathVariable(name = "track-id") String trackId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("최근 들은 음악 목록에 트랙 게시");
		trackService.createRecentTrack(trackId, userDetails.getUser());
		return ResponseEntity.ok("최근 들은 목록에 트랙이 추가되었습니다.");
	}

	@GetMapping("/search/recommend-keywords")
	public ResponseEntity<List<Top7Dto>> get7RecentTracks() {
		logger.info("들은 음악 중 top7 트랙 가져오기");
		return ResponseEntity.ok(trackService.get7RecentTracks());
	}

	@PutMapping("/{track-id}/star")
	public ResponseEntity<StatusResponseDto> setStarRating(@PathVariable(name = "track-id") String trackId,
		@Valid @RequestBody StarDto starDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("별점 매기기");
		return trackService.setStarRating(trackId, starDto, userDetails);
	}

	@DeleteMapping("/{track-id}/star")
	public ResponseEntity<StatusResponseDto> deleteStarRating(@PathVariable(name = "track-id") String trackId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("별점 삭제하기");
		return trackService.deleteStarRating(trackId, userDetails);
	}

	@GetMapping("/{track-id}/starList")
	public ResponseEntity<List<StarListResponseDto>> getStarList(@PathVariable(name = "track-id") String trackId) {
		logger.info("트랙의 별점 리스트 가져오기");
		return trackService.getStarList(trackId);
	}
}
