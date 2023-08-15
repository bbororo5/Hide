package com.example.backend.playlist.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "music")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    private String albumUrl;
    private String artist;
    private String genre;
    private String title;

    public String getAlbumUrl() {
        return albumUrl;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }
}
