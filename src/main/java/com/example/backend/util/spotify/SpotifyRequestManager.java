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
		logger.info("스포티파이에서 트랙 ID {}에 대한 정보를 가져오는 중", trackIds);
		return trackSpotifyRequest.fetchDataTrackList(String.join(",", trackIds));
	}

	public Track getTrackInfo(String trackId) {
		logger.info("스포티파이에서 트랙 ID {}에 대한 정보를 가져오는 중", trackId);
		return trackSpotifyRequest.fetchDataOneTrack(trackId);
	}

	public List<Track> getRecommendTracks(List<String> trackIds) {
		logger.info("스포티파이에서 트랙 ID {}에 대한 추천 트랙을 가져오는 중", trackIds);
		return recommendationSpotifyRequest.fetchData(String.join(",", trackIds));
	}

	public List<Track> getSearchResult(String keyword) {
		logger.info("스포티파이에 키워드 '{}'를 사용하여 검색 중", keyword);
		return searchSpotifyRequest.fetchData(keyword);
	}
}

