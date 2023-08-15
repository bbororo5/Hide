package com.example.backend.playlist.controller;//package com.example.playlist.playlist.controller;
//
//import com.example.backend.music.dto.MusicResponseDto;
//import com.example.backend.playlist.dto.PlaylistRequestDto;
//import com.example.backend.playlist.service.PlaylistService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/user/{user-id}")
//public class PlaylistController {
//    private final PlaylistService playlistService;
//
//    public PlaylistController(PlaylistService playlistService) {
//        this.playlistService = playlistService;
//    }
//
//    @GetMapping("/playlist")
//    public ResponseEntity<List<MusicResponseDto>> getPlaylist(@PathVariable("userId") String userId) {
//        List<MusicResponseDto> musicDtoList = playlistService.getPlaylist(userId);
//        return ResponseEntity.ok(musicDtoList);
//    }
//
//    @PostMapping("/playlist")
//    public ResponseEntity<List<MusicResponseDto>> postPlaylist(@PathVariable("userId") String userId,
//                                                               @RequestBody PlaylistRequestDto playlistRequestDto) {
//        List<MusicResponseDto> updatedPlaylist = playlistService.addMusicToPlaylist(userId, playlistRequestDto);
//        return ResponseEntity.ok(updatedPlaylist);
//    }
//
//    @DeleteMapping("/playlist/{music-id}")
//    public ResponseEntity<List<MusicResponseDto>> deleteMusicFromPlaylist(@PathVariable("userId") String userId,
//                                                                          @PathVariable String musicId) {
//        List<MusicResponseDto> updatedPlaylist = playlistService.deleteMusicFromPlaylist(userId, musicId);
//        return ResponseEntity.ok(updatedPlaylist);
//    }
//}
