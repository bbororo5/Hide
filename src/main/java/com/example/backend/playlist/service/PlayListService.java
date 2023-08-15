package com.example.backend.playlist.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.StatusResponseDto;
import com.example.backend.playlist.dto.PlaylistDto;
import com.example.backend.playlist.entity.Playlist;
import com.example.backend.playlist.repository.PlayListRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.SpotifyUtil;
import com.example.backend.util.spotify.dto.Track;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayListService {
	private final SpotifyUtil spotifyUtil;
	private final UserRepository userRepository;
	private final PlayListRepository playListRepository;

	@Transactional
	public ResponseEntity<StatusResponseDto> addTrackToPlaylist(String trackId, UserDetailsImpl userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
		Playlist playlist = new Playlist(trackId, user);
		playListRepository.save(playlist);
		return new ResponseEntity<>(new StatusResponseDto("플레이 리스트에 음악을 추가했습니다.", true), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> deleteTrackFromPlaylist(Long id, UserDetailsImpl userDetails) {
		Playlist playlist = playListRepository.findByIdAndUser(id, userDetails.getUser())
			.orElseThrow(() -> new NullPointerException("플레이 리스트가 존재하지 않습니다."));
		playListRepository.delete(playlist);
		return new ResponseEntity<>(new StatusResponseDto("플레이 리스트에서 삭제했습니다.", true), HttpStatus.OK);
	}

	public List<PlaylistDto> getPlaylist(Long userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
		List<Playlist> playlists = user.getPlaylists();
		playlists.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		List<PlaylistDto> playlistDtoList = new ArrayList<>();
		List<String> trackIds = new ArrayList<>();

		for (Playlist playlist : playlists) {
			PlaylistDto playlistDto = new PlaylistDto(playlist);
			playlistDtoList.add(playlistDto);
			trackIds.add(playlist.getTrackId());
		}

		List<Track> trackList = spotifyUtil.getTracksInfo(trackIds);

		for(int i = 0; i<playlistDtoList.size();i++){
			playlistDtoList.get(i).setPlaylistDto(trackList.get(i));
		}

		return playlistDtoList;
	}
}
