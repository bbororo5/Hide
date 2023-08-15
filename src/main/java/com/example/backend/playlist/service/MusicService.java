//package com.example.backend.playlist.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class MusicService {
//
//    private final MusicRepository musicRepository;
//
//    @Autowired
//    public MusicService(MusicRepository musicRepository) {
//        this.musicRepository = musicRepository;
//    }
//
//    public MusicResponseDto getMusicDetail(Long id) {
//        Music music = musicRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("음악을 찾을 수 없습니다."));
//        return new MusicResponseDto(music);
//    }
//
//    public List<MusicResponseDto> searchByTitle(String title) {
//        return convertToResponse(musicRepository.findByTitle(title));
//    }
//
//    public List<MusicResponseDto> searchByArtist(String artist) {
//        return convertToResponse(musicRepository.findByArtist(artist));
//    }
//
//    private List<MusicResponseDto> convertToResponse(List<Music> musics) {
//        return musics.stream()
//                .map(MusicResponseDto::new)
//                .collect(Collectors.toList());
//    }
//}
//}
