package com.example.backend.util.spotify.service;

import com.example.backend.util.spotify.dto.Track;
import com.example.backend.user.entity.User;
//import com.example.backend.user.repository.RecentRepository;
import com.example.backend.user.repository.RecentRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.UserNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
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
@RequiredArgsConstructor
public class SpotifyService {

    private String accessToken;
    private final RecentRepository recentRepository;
    private final UserRepository userRepository;

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

    public List<Track> getTracksInfo(List<String> trackIds) {
        return getTracksInfo(trackIds, 0);
    }

    private List<Track> getTracksInfo(List<String> trackIds, int attempt) {
        if (attempt > 2) {
            throw new IllegalStateException("재시도 2회 후에도 트랙 정보 가져오기 실패");
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        String ids = String.join(",", trackIds);
        String url = String.format("https://api.spotify.com/v1/tracks?ids=%s", ids);

        ArrayList<Track> tracklist = new ArrayList<>();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            JsonNode responseBody = response.getBody();
            JsonNode tracksNode = responseBody.path("tracks");

            if (tracksNode.isMissingNode()) {
                throw new RuntimeException("노드를 찾을 수 없습니다.");
            }

            for (JsonNode trackNode : tracksNode) {
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
                String imageUrl640 = imageNodes.size() > 0 ? imageNodes.get(0).path("url").asText() : "";

                List<Track.Artist> artists = new ArrayList<>();
                JsonNode artistNodes = trackNode.path("artists");
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
                        .artists(artists)
                        .build();
                tracklist.add(track);
            }
        } catch (RestClientException e) {
            if (isTokenExpired(e)) {
                requestAccessToken();
                return getTracksInfo(trackIds, attempt + 1); // 재시도
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

    public List<Track> getRecentTracks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));
        List<String> trackIds = recentRepository.findTrackIdByUserOrderByCreationDateDesc(user);
        return getTracksInfo(trackIds);
    }
}

