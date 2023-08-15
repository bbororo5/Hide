package com.example.backend.playlist.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.StatusResponseDto;
import com.example.backend.playlist.dto.PlaylistDto;
import com.example.backend.playlist.entity.Playlist;
import com.example.backend.playlist.service.PlayListService;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.dto.Track;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlayListController {
	private final PlayListService playListService;

	@PostMapping("/playlist/{track-id}")
	public ResponseEntity<StatusResponseDto> addTrackToPlaylist(
		@PathVariable(name = "track-id") String trackId, @AuthenticationPrincipal
	UserDetailsImpl userDetails) {
		return playListService.addTrackToPlaylist(trackId, userDetails);
	}

	@DeleteMapping("/playlist/{playlist-id}")
	public ResponseEntity<StatusResponseDto> deleteTrackFromPlaylist(@PathVariable(name = "playlist-id") Long id,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return playListService.deleteTrackFromPlaylist(id, userDetails);
	}

	@GetMapping("/playlist/{user-id}")
	public List<PlaylistDto> getPlaylist(@PathVariable(name = "user-id") Long userId) {
		return playListService.getPlaylist(userId);
	}
}
