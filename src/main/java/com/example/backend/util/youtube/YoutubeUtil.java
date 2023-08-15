package com.example.backend.util.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class YoutubeUtil {

    private final String apiKey = "AIzaSyCGPwkCWI-8i5-_VOq3qEETCcswDz1_QQQ";

    private final RestTemplate restTemplate;

    public String getVideoId(String keyword) {
        // URL 구성
        String baseUrl = "https://youtube.googleapis.com/youtube/v3/search";
        String finalUrl = baseUrl + "?part=snippet&maxResults=1&q=" + keyword + "&type=video&key=" + apiKey;

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        // 요청 실행
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                entity,
                JsonNode.class);

        JsonNode responseBody = response.getBody();
        JsonNode idNode = responseBody.path("items").get(0).path("id");

        String videoId = idNode
                .path("videoId")
                .asText();

        return videoId;
    }
}

