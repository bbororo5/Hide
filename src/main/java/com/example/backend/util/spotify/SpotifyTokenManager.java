package com.example.backend.util.spotify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SpotifyTokenManager {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyTokenManager.class);
    private static final long FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000L;
    private static final String REDIS_ACCESS_TOKEN_KEY = "spotify:access_token";
    private static final String REDIS_EXPIRATION_TIME_KEY = "spotify:token_expiration_time";
    private StringRedisTemplate redisTemplate;
    private final Object lock = new Object();
    @Value("${spotify.clientId}")
    private final String SPOTIFY_CLIENT_ID;
    @Value("${spotify.clientSecret}")
    private final String SPOTIFY_CLIENT_SECRET;

    public String getAccessToken() {
        logger.info("Redis에서 토큰 가져오기");
        String redisAccessToken = redisTemplate.opsForValue().get(REDIS_ACCESS_TOKEN_KEY);
        String redisExpirationTime = redisTemplate.opsForValue().get(REDIS_EXPIRATION_TIME_KEY);

        if (redisAccessToken == null || redisExpirationTime == null) {
            logger.warn("액세스 토큰 또는 토큰의 만료기간이 null을 가지고 있습니다.");
            requestAccessToken();
            return redisTemplate.opsForValue().get(REDIS_ACCESS_TOKEN_KEY);
        }

        logger.info("액세스 토큰 유효성 검사");
        if (!isValid(redisAccessToken, Long.parseLong(redisExpirationTime))) {
            logger.info("액세스 토큰이 유효하지 않습니다.");
            requestAccessToken();
        }

        return redisTemplate.opsForValue().get(REDIS_ACCESS_TOKEN_KEY);
    }

    private synchronized void requestAccessToken() {
        logger.info("Access Token 요청 메서드 시작");

        logger.info("토큰 유효성 재확인");
        String redisAccessToken = redisTemplate.opsForValue().get(REDIS_ACCESS_TOKEN_KEY);
        String redisExpirationTime = redisTemplate.opsForValue().get(REDIS_EXPIRATION_TIME_KEY);

        if (isValid(redisAccessToken, Long.parseLong(redisExpirationTime))) {
            return;
        }
        logger.info("다른 스레드에서 갱신된 Access Token이 없음을 확인");

        String credentials = new String(Base64.getEncoder().encode((SPOTIFY_CLIENT_ID + ":" + SPOTIFY_CLIENT_SECRET).getBytes(StandardCharsets.UTF_8)));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + credentials);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

        RestTemplate restTemplate = new RestTemplate();
        logger.info("스포티파이에 Access Token 요청 시작");
        ResponseEntity<String> response = restTemplate.exchange(
                "https://accounts.spotify.com/api/token",
                HttpMethod.POST,
                entity,
                String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String newAccessToken = jsonNode.get("access_token").asText();
            long newTokenExpirationTime = System.currentTimeMillis() + (jsonNode.get("expires_in").asInt() * 1000L);

            logger.info("Access Token 성공적으로 받아옴. 만료 시간: {}", newTokenExpirationTime);

            redisTemplate.opsForValue().set(REDIS_ACCESS_TOKEN_KEY, newAccessToken);
            redisTemplate.opsForValue().set(REDIS_EXPIRATION_TIME_KEY, String.valueOf(newTokenExpirationTime));

            logger.info("Access Token과 만료 시간을 Redis에 저장함");

        } catch (JsonProcessingException e) {
            logger.error("JSON 파싱 오류 발생", e);

        } catch (RestClientException e) {
            logger.error("스포티파이 API 요청 중 오류 발생", e);

        } catch (Exception e) {
            logger.error("알 수 없는 오류 발생", e);
        }

        logger.info("Access Token 요청 메서드 종료");
    }

    private boolean isValid(String token, long expirationTime) {
        return token != null &&
                !token.isEmpty() &&
                (expirationTime - System.currentTimeMillis()) > FIVE_MINUTES_IN_MILLIS;
    }
}

