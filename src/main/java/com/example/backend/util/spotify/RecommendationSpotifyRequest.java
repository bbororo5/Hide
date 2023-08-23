package com.example.backend.util.spotify;

import com.example.backend.track.dto.Track;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationSpotifyRequest extends AbstractSpotifyRequest {
    public RecommendationSpotifyRequest(SpotifyTokenManager spotifyTokenManager) {
        super(spotifyTokenManager);
    }


    public List<Track> fetchData(String trackId) {
        return super.fetchDataFromSpotifyAPI(trackId,  0);
    }

    @Override
    protected String generateSpotifyUrl(String trackId) {
        return generateSpotifyUrl(new String[]{trackId});
    }

    protected String generateSpotifyUrl(String... trackIds) {
        return String.format("https://api.spotify.com/v1/recommendations?limit=10&seed_tracks=%s", String.join(",", trackIds));
    }

    @Override
    protected JsonNode extractTracksNode(JsonNode responseBody) {
        return responseBody.path("tracks");
    }
}
