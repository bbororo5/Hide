package com.example.backend.playlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistRequestDto {
    private String title;
    private String artist;
    private String albumUrl;
    private String genre;

    public PlaylistRequestDto(String title, String artist, String albumUrl, String genre) {
        this.title = title;
        this.artist = artist;
        this.albumUrl = albumUrl;
        this.genre = genre;
    }
}
