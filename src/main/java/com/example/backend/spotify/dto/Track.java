package com.example.backend.spotify.dto;

import lombok.Builder;
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
    public static class Artist {
        private String artistName;

        @Builder
        public Artist(String artistName) {
            this.artistName = artistName;
        }

    }

    @Builder
    public Track(String trackTitle, String albumName, String album640Image,
                 String album300Image, String album64Image, List<Artist> artists) {
        this.trackTitle = trackTitle;
        this.albumName = albumName;
        this.album640Image = album640Image;
        this.album300Image = album300Image;
        this.album64Image = album64Image;
        this.artists = artists;
    }
}
