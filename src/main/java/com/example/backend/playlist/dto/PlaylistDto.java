package com.example.backend.playlist.dto;

import java.time.LocalDateTime;

import com.example.backend.playlist.entity.Playlist;
import com.example.backend.track.dto.Track;

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
		this.trackTitle = track.getTitle();
		this.albumName =track.getAlbum();
		this.album640Image = track.getImage();
		this.artistsStringList = track.getArtistsStringList();
	}
}
