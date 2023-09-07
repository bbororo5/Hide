package com.example.backend.track.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrackDetailModal {
	private String image;
	private String album;
	private String artist;
	private String title;
}
