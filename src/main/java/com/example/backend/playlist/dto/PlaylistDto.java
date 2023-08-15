package com.example.backend.playlist.dto;

import java.time.LocalDateTime;

import com.example.backend.playlist.entity.Playlist;
import com.example.backend.util.spotify.dto.Track;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaylistDto {
	private Long playlistId;
	private String trackId;
	private String trackTitle;
	private String albumName;
	private String album640Image;
	private String artistsStringList;
	private LocalDateTime createdAt;

	public PlaylistDto(Playlist playlist) {
		this.playlistId = playlist.getId();
		this.trackId = playlist.getTrackId();
		this.createdAt = playlist.getCreatedAt();
	}
	public void setPlaylistDto(Track track) {
		this.trackTitle = track.getTrackTitle();
		this.albumName =track.getAlbumName();
		this.album640Image = track.getAlbum640Image();
		this.artistsStringList = track.getArtistsStringList();
	}
}
