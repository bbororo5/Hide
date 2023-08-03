package com.example.backend.spotify.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class Track {
    private String trackTitle;
    private String albumName;
    private String album640Image;
    private String album300Image;
    private String album64Image;
    private List<Artist> artists;

    @Getter
    public class Artist {
        private String artistName;

    }
}
