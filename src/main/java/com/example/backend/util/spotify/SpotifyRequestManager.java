package com.example.backend.util.spotify;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.backend.track.dto.Track;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpotifyRequestManager {
	private final RecommendationSpotifyRequest recommendationSpotifyRequest;
	private final TrackSpotifyRequest trackSpotifyRequest;
	private final SearchSpotifyRequest searchSpotifyRequest;
	private static final Logger logger = LoggerFactory.getLogger(SpotifyRequestManager.class);

	public List<Track> getTracksInfo(List<String> trackIds) {
		return trackSpotifyRequest.fetchDataTrackList(String.join(",", trackIds));
	}

	public Track getTrackInfo(String trackId) {
		return trackSpotifyRequest.fetchDataOneTrack(trackId);
	}

	public List<Track> getRecommendTracks(List<String> trackIds) {
		return recommendationSpotifyRequest.fetchData(String.join(",", trackIds));
	}

	public List<Track> getSearchResult(String keyword) {
		return searchSpotifyRequest.fetchData(keyword);
	}
}

