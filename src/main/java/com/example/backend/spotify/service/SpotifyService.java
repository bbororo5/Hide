package com.example.backend.spotify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    public void getPopularMusics() {

    }
}
