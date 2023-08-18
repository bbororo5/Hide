package com.example.backend.playlist.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.playlist.entity.Playlist;
import com.example.backend.track.dto.Track;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaylistDto {
	private Long playlistId;
	private String trackId;
	private String title;
	private String album;
	private String image;
	private List<Track.Artist> artists;
	private String artistsStringList;
	private LocalDateTime createdAt;


	@Getter
	public static class Artist {
		private String artistName;

		@Builder
		public Artist(String artistName) {
			this.artistName = artistName;
		}

	}
	public PlaylistDto(Playlist playlist) {
		this.playlistId = playlist.getId();
		this.trackId = playlist.getTrackId();
		this.createdAt = playlist.getCreatedAt();
	}
	public void setPlaylistDto(Track track) {
		this.title = track.getTitle();
		this.album =track.getAlbum();
		this.image = track.getImage();
		this.artists = track.getArtists();
		this.artistsStringList = track.getArtistsStringList();
	}
}
