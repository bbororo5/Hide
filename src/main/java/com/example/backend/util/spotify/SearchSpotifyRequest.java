package com.example.backend.util.spotify;

import com.example.backend.track.dto.Track;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SearchSpotifyRequest extends AbstractSpotifyRequest {
    public SearchSpotifyRequest(SpotifyTokenManager spotifyTokenManager , RestTemplate restTemplate) {
        super(spotifyTokenManager , restTemplate);
    }

    public List<Track> fetchData(String keyword) {
        return super.fetchDataFromSpotifyAPI(keyword, 0);
    }

    @Override
    protected String generateSpotifyUrl(String keyword) {
        return String.format("https://api.spotify.com/v1/search?q=%s&type=track&limit=20", keyword);
    }

    @Override
    protected JsonNode extractTracksNode(JsonNode responseBody) {
        return responseBody.path("tracks").path("items");
    }
}
