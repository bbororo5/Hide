package com.example.backend.spotify.service;

import com.example.backend.spotify.dto.Track;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Service
public class SpotifyService {

    private String accessToken;

    public void requestAccessToken() {
        String clientId = "f780b05092934735af74590a2db00115";
        String clientSecret = "42de17c59f66414f9bcbb95ba597b59a";
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        WebClient webClient = WebClient.builder()
                .baseUrl("https://accounts.spotify.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        Mono<String> responseMono = webClient.post()
                .uri("/api/token")
                .header("Authorization", "Basic " + credentials)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(String.class);

        responseMono.subscribe(response -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response);
                this.accessToken = jsonNode.get("access_token").asText();
                System.out.println(accessToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<Track> getPopularMusics() {
        String resourcePath = "/v1/playlists/37i9dQZF1DXcBWIGoYBM5M";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ArrayList<Track> tracklist = new ArrayList<>();
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    ("https://api.spotify.com" + resourcePath),
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            JsonNode responseBody = response.getBody();
            JsonNode items = responseBody.path("tracks").path("items");

            if (items.isMissingNode()) {
                // 노드가 누락되었을 때의 처리
                throw new RuntimeException("노드를 찾을 수 없습니다.");
            }

            int count = 0;

            for (JsonNode item : items) {
                if (count == 10) {
                    break;
                }

                String trackTitle = item
                        .path("track")
                        .path("name")
                        .asText();

                String albumName = item.path("track")
                        .path("album")
                        .path("name")
                        .asText();

                JsonNode imageNodes = item.path("track")
                        .path("album")
                        .path("images");
                String imageUrl640 = imageNodes.get(0).path("url").asText();
                String imageUrl300 = imageNodes.get(1).path("url").asText();
                String imageUrl64 = imageNodes.get(2).path("url").asText();

                List<Track.Artist> artists = new ArrayList<>();
                JsonNode artistNodes = item.path("track").path("artists");
                for (JsonNode artistNode : artistNodes) {
                    String artistName = artistNode.path("name").asText();
                    Track.Artist artist = Track.Artist.builder()
                            .artistName(artistName)
                            .build();
                    artists.add(artist);
                }

                Track track = Track.builder()
                        .trackTitle(trackTitle)
                        .albumName(albumName)
                        .album640Image(imageUrl640)
                        .album300Image(imageUrl300)
                        .album64Image(imageUrl64)
                        .artists(artists)
                        .build();
                tracklist.add(track);

                count++;
            }
        } catch (RestClientException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }

        return tracklist;
    }
}

