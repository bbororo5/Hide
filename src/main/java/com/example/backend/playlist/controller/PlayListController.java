package com.example.backend.playlist.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.util.globalDto.StatusResponseDto;
import com.example.backend.playlist.dto.PlaylistDto;
import com.example.backend.playlist.service.PlayListService;
import com.example.backend.util.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlayListController {
	private static final Logger logger = LoggerFactory.getLogger(PlayListController.class);
	private final PlayListService playListService;

	@PostMapping("/playlist/{track-id}")
	public ResponseEntity<StatusResponseDto> addTrackToPlaylist(
		@PathVariable(name = "track-id") String trackId, @AuthenticationPrincipal
	UserDetailsImpl userDetails) {
		logger.info("플레이리스트에 트랙 추가");
		return playListService.addTrackToPlaylist(trackId, userDetails);
	}

	@DeleteMapping("/playlist/{playlist-id}")
	public ResponseEntity<StatusResponseDto> deleteTrackFromPlaylist(@PathVariable(name = "playlist-id") Long playListId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("플레이리스트에 트랙 제거");
		return playListService.deleteTrackFromPlaylist(playListId, userDetails);
	}

	@GetMapping("/playlist/{user-id}")
	public List<PlaylistDto> getPlaylist(@PathVariable(name = "user-id") Long userId) {
		logger.info("플레이리스트 조회");
		return playListService.getPlaylist(userId);
	}
}
