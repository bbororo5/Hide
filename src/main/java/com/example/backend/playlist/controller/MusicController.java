package main.java.com.example.backend.playlist.controller;//package com.example.playlist.playlist.controller;
//
//import com.example.playlist.music.dto.MusicResponseDto;
//import com.example.playlist.playlist.service.MusicService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/musics")
//public class MusicController {
//    private final MusicService musicService;
//
//    public MusicController(MusicService musicService) {
//        this.musicService = musicService;
//    }
//
//    @GetMapping("/modal")
//    public ResponseEntity<MusicResponseDto> getMusicDetail(@PathVariable Long id) {
//        MusicResponseDto musicDetail = musicService.getMusicDetail(id);
//        return ResponseEntity.ok(musicDetail);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<MusicResponseDto>> searchByTitle(@RequestParam String title) {
//        List<MusicResponseDto> searchResult = musicService.searchByTitle(title);
//        return ResponseEntity.ok(searchResult);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<MusicResponseDto>> searchByArtist(@RequestParam String artist) {
//        List<MusicResponseDto> searchResult = musicService.searchByArtist(artist);
//        return ResponseEntity.ok(searchResult);
//    }
//}
