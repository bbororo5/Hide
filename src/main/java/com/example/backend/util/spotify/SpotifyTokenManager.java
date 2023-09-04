package com.example.backend.util.spotify;

import com.example.backend.util.RedisUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SpotifyTokenManager {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyTokenManager.class);
    private final RedisUtil redisUtil;
    @Value("${spotify.clientId}")
    private final String SPOTIFY_CLIENT_ID;
    @Value("${spotify.clientSecret}")
    private final String SPOTIFY_CLIENT_SECRET;

    public String getAccessToken() {
        logger.info("Redis로부터 accessToken 조회 시작");
        String token = redisUtil.getAccessToken(RedisUtil.ACCESS_TOKEN_KEY);

        if (token == null) {
            logger.info("accessToken이 만료됨");
            requestAccessToken();
        }

        logger.info("Redis로부터 accessToken 조회 완료, accessToken 수령");
        return token;
    }

    private synchronized void requestAccessToken() {
        logger.info("Access Token 요청 메서드 시작");

        if (redisUtil.getAccessToken(RedisUtil.ACCESS_TOKEN_KEY) != null) {
            logger.info("유효한 Access Token이 기존재");
            return;
        }

        logger.info("다른 스레드에 갱신된 Access Token이 없음을 확인");
        String credentials = new String(Base64.getEncoder().encode((SPOTIFY_CLIENT_ID + ":" + SPOTIFY_CLIENT_SECRET).getBytes(StandardCharsets.UTF_8)));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + credentials);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

        RestTemplate restTemplate = new RestTemplate();
        logger.info("스포티파이에 Access Token 요청 시작");
        ResponseEntity<String> response = restTemplate.exchange("https://accounts.spotify.com/api/token", HttpMethod.POST, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
            logger.info("Access Token 성공적으로 받아옴.");

            redisUtil.saveAccessToken(accessToken);
            logger.info("Access Token 성공적으로 인메모리화.");

        } catch (Exception e) {
            logger.error("Access Token 응답 파싱 중 오류 발생", e);
        }

        logger.info("Access Token 요청 메서드 종료");
    }
}


