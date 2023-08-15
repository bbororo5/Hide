package com.example.backend.util.spotify.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class Track {
    private String trackTitle;
    private String albumName;
    private String album640Image;
    private List<Artist> artists;
    private String artistsStringList;

    @Getter
    public static class Artist {
        private String artistName;

        @Builder
        public Artist(String artistName) {
            this.artistName = artistName;
        }

    }

    @Builder
    public Track(String trackTitle, String albumName, String album640Image, List<Artist> artists) {
        StringBuilder sb = new StringBuilder();
        this.trackTitle = trackTitle;
        this.albumName = albumName;
        this.album640Image = album640Image;
        this.artists = artists;
        for(Artist artist : artists){
            sb.append(artist.getArtistName()+", ");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2); // 마지막 쉼표 제거
        }
        this.artistsStringList = sb.toString();
    }
}
