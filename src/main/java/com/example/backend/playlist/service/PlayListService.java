package com.example.backend.playlist.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.util.globalDto.StatusResponseDto;
import com.example.backend.playlist.dto.PlaylistDto;
import com.example.backend.playlist.entity.Playlist;
import com.example.backend.playlist.repository.PlayListRepository;
import com.example.backend.track.dto.Track;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.SpotifyRequestManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayListService {
	private static final Logger logger = LoggerFactory.getLogger(PlayListService.class);
	private final SpotifyRequestManager spotifyUtil;
	private final UserRepository userRepository;
	private final PlayListRepository playListRepository;

	@Transactional
	public ResponseEntity<StatusResponseDto> addTrackToPlaylist(String trackId, UserDetailsImpl userDetails) {
		logger.info("플레이리스트에 트랙 추가 시작");
		User user = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
		Playlist newPlaylist = new Playlist(trackId, user);
		Playlist playlist = playListRepository.findByTrackIdAndUser(trackId,user).orElse(null);
		if(playlist!=null){
			playListRepository.delete(playlist);
			playListRepository.save(newPlaylist);
		}else{
			playListRepository.save(newPlaylist);
		}
		logger.info("플레이리스트에 트랙 추가 완료");
		return new ResponseEntity<>(new StatusResponseDto("플레이 리스트에 음악을 추가했습니다.", true), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> deleteTrackFromPlaylist(Long playListId, UserDetailsImpl userDetails) {
		logger.info("플레이리스트에 트랙 삭제 시작. playList ID: {}", playListId);
		Playlist playlist = playListRepository.findByIdAndUser(playListId, userDetails.getUser())
			.orElseThrow(() -> new NoSuchElementException("플레이 리스트가 존재하지 않습니다."));
		playListRepository.delete(playlist);
		logger.info("플레이리스트에 트랙 삭제 완료. playList ID: {}", playListId);
		return new ResponseEntity<>(new StatusResponseDto("플레이 리스트에서 삭제했습니다.", true), HttpStatus.OK);
	}

	@Transactional(readOnly = true)
	public List<PlaylistDto> getPlaylist(Long userId) {
		logger.info("플레이리스트 조회 시작");
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
		List<Playlist> playlists = user.getPlaylists();
		if(playlists.isEmpty()){
			return Collections.emptyList();
		}
		playlists.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		List<PlaylistDto> playlistDtoList = new ArrayList<>();
		List<String> trackIds = new ArrayList<>();

		for (Playlist playlist : playlists) {
			PlaylistDto playlistDto = new PlaylistDto(playlist);
			playlistDtoList.add(playlistDto);
			trackIds.add(playlist.getTrackId());
		}

		List<Track> trackList = spotifyUtil.getTracksInfo(trackIds);

		for (int i = 0; i < playlistDtoList.size(); i++) {
			playlistDtoList.get(i).setPlaylistDto(trackList.get(i));
		}
		logger.info("플레이리스트 조회 완료: {} 개의 트랙", playlists.size());

		return playlistDtoList;
	}
}
