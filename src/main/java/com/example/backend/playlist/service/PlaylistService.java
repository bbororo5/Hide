//package com.example.backend.playlist.service;
//
//import com.example.backend.music.dto.MusicResponseDto;
//import com.example.backend.playlist.dto.PlaylistRequestDto;
//import com.example.backend.playlist.entity.Playlist;
//import com.example.backend.playlist.repository.PlaylistRepository;
//import com.example.backend.music.entity.Music;
//import com.example.backend.music.repository.MusicRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class PlaylistService {
//
//    private final PlaylistRepository playlistRepository;
//    private final MusicRepository musicRepository;
//
//    @Autowired
//    public PlaylistService(PlaylistRepository playlistRepository, MusicRepository musicRepository) {
//        this.playlistRepository = playlistRepository;
//        this.musicRepository = musicRepository;
//    }
//
//    public List<MusicResponseDto> getPlaylist(String userId) {
//        Playlist playlist = playlistRepository.findByUserId(userId);
//        return playlist.getMusics().stream()
//                .map(music -> new MusicResponseDto(music))
//                .collect(Collectors.toList());
//    }
//
//    public List<MusicResponseDto> addMusicToPlaylist(String userId, PlaylistRequestDto playlistRequestDto) {
//        Playlist playlist = playlistRepository.findByUserId(userId);
//        Music music = new Music(playlistRequestDto);
//        playlist.getMusics().add(music);
//        playlistRepository.save(playlist);
//        return getPlaylist(userId);
//    }
//
//    public List<MusicResponseDto> deleteMusicFromPlaylist(String userId, Long musicId) {
//        Playlist playlist = playlistRepository.findByUserId(userId);
//        Music music = musicRepository.findById(musicId).orElseThrow(() -> new RuntimeException("음악을 찾을 수 없습니다."));
//        playlist.getMusics().remove(music);
//        playlistRepository.save(playlist);
//        return getPlaylist(userId);
//    }
//}
