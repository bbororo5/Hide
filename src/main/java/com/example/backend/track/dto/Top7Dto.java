package com.example.backend.track.dto;

import lombok.Getter;

@Getter
public class Top7Dto {
	private String trackId;
	private String title;

	public Top7Dto(Track track){
		this.trackId= track.getTrackId();
		this.title= track.getTitle();
	}
}
