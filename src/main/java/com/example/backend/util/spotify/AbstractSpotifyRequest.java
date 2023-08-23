package com.example.backend.util.spotify;

import com.example.backend.track.dto.Track;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@RequiredArgsConstructor
public abstract class AbstractSpotifyRequest {
    protected final SpotifyTokenManager spotifyTokenManager;

    protected List<Track> fetchDataFromSpotifyAPI(String parameter, int attempt) {
        if (attempt > 2) {
            throw new IllegalStateException("재시도 2회 후에도 트랙 정보 가져오기 실패");
        }
        spotifyTokenManager.getAccessToken();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + spotifyTokenManager.accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        String url = generateSpotifyUrl(parameter);

        ArrayList<Track> tracklist = new ArrayList<>();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            JsonNode responseBody = response.getBody();
            JsonNode tracksNode = extractTracksNode(responseBody);

            if (tracksNode.isMissingNode()) {
                throw new NoSuchElementException("노드를 찾을 수 없습니다.");
            }

            for (JsonNode trackNode : tracksNode) {
                tracklist.add(parseTrackNode(trackNode));
            }
        } catch (RestClientException e) {
            if (isTokenExpired(e)) {
                spotifyTokenManager.getAccessToken();
                return fetchDataFromSpotifyAPI(parameter, attempt + 1); // 재시도
            } else {
                throw e;
            }
        }

        return tracklist;
    }

    private boolean isTokenExpired(RestClientException e) {
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException httpException = (HttpStatusCodeException) e;

            //401 여부 체크
            if (httpException.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                //반환되는 메세지 확인하여 토큰 만료 여부 확인
                return httpException.getResponseBodyAsString().contains("The access token expired");
            }
        }

        return false;
    }
    private Track parseTrackNode(JsonNode trackNode) {
        String trackId = trackNode
                .path("id")
                .asText();

        String trackTitle = trackNode
                .path("name")
                .asText();

        String albumName = trackNode
                .path("album")
                .path("name")
                .asText();

        JsonNode imageNodes = trackNode
                .path("album")
                .path("images");

        String image = imageNodes.size() > 0 ? imageNodes.get(0).path("url").asText() : "";

        List<Track.Artist> artists = new ArrayList<>();
        JsonNode artistNodes = trackNode.path("artists");
        for (JsonNode artistNode : artistNodes) {
            String artistName = artistNode.path("name").asText();
            Track.Artist artist = Track.Artist.builder()
                    .artistName(artistName)
                    .build();
            artists.add(artist);
        }

        List<Track.Genre> genreList = new ArrayList<>();
        JsonNode genreNodes = trackNode
                .path("album");
        for (JsonNode genreNode : genreNodes) {
            String genre = genreNode.path("genre").asText();
            Track.Genre genreElement = Track.Genre.builder()
                    .genre(genre)
                    .build();
            genreList.add(genreElement);
        }

        return Track.builder()
                .id(trackId)
                .title(trackTitle)
                .album(albumName)
                .image(image)
                .artists(artists)
                .genre(genreList)
                .build();
    }
    protected abstract String generateSpotifyUrl(String parameter);

    protected abstract JsonNode extractTracksNode(JsonNode responseBody);
}
