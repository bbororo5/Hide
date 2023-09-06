package com.example.backend.track.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class TrackDetailDto {
	private String trackId;
	private String title;
	private String album;
	private String image;
	private Double averageStar;
	private List<Track.Artist> artists;
	private String artistsStringList;
	// private List<Track.Genre> genre;

	public TrackDetailDto(Track track,Double averageStar) {
		this.trackId = track.getTrackId();
		this.title = track.getTitle();
		this.album = track.getAlbum();
		this.image = track.getImage();
		this.artists = track.getArtists();
		this.artistsStringList = track.getArtistsStringList();
		// this.genre = track.getGenre();
		if(averageStar!=null){
			this.averageStar= Math.round(averageStar * 10.0) / 10.0;
		}
	}

}
