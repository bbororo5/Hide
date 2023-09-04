package com.example.backend.track.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Track {
	private String trackId;
	private String title;
	private String album;
	private String image;
	private List<Artist> artists;
	private String artistsStringList;
	private List<Genre> genre;

	@Getter
	public static class Artist {
		private String artistName;

		@Builder
		public Artist(String artistName) {
			this.artistName = artistName;
		}

	}

	@Getter
	public static class Genre {
		private String genre;

		@Builder
		public Genre(String genre) {
			this.genre = genre;
		}

	}

	@Builder
	public Track(String id, String title, String album, String image, List<Artist> artists, List<Genre> genre) {
		StringBuilder sb = new StringBuilder();
		this.trackId = id;
		this.title = title;
		this.album = album;
		this.image = image;
		this.artists = artists;
		for (Artist artist : artists) {
			sb.append(artist.getArtistName() + ", ");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 2); // 마지막 쉼표 제거
		}
		this.artistsStringList = sb.toString();
		this.genre = genre;
	}
}
