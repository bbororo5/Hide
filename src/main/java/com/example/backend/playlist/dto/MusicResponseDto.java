package com.example.backend.playlist.dto;//package com.example.playlist.playlist.dto;

import lombok.Getter;
import com.example.backend.playlist.entity.Music;

@Getter
public class MusicResponseDto {
    private String albumUrl;
    private String artist;
    private String genre;
    private String title;

    public MusicResponseDto(Music music) {
        this.albumUrl = music.getAlbumUrl();
        this.artist = music.getArtist();
        this.genre = music.getGenre();
        this.title = music.getTitle();
    }
}
