package com.example.backend.util.spotify;

import com.example.backend.util.RedisUtil;
import com.example.backend.util.execption.TokenNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
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

        String accessToken;
        try {
            accessToken = redisUtil.getAccessToken(RedisUtil.ACCESS_TOKEN_KEY);
        } catch (TokenNotFoundException e) {
            logger.warn("Redis에 accessToken이 없음. 새로 요청합니다.", e);
            accessToken = requestAccessToken();
        }

        logger.info("Redis로부터 accessToken 조회 완료, accessToken 수령");
        return accessToken;
    }


    private synchronized String requestAccessToken() {
        logger.info("Access Token 요청 메서드 시작");
        String accessToken = null;

        // 이미 존재하는 토큰 확인
        try {
            String existingToken = redisUtil.getAccessToken(RedisUtil.ACCESS_TOKEN_KEY);
            if (existingToken != null) {
                logger.info("유효한 Access Token이 기존재");
                return existingToken;
            }
        } catch (Exception e) {
            logger.error("Redis에서 Access Token을 조회하는 도중 오류 발생", e);
        }

        logger.info("다른 스레드에 갱신된 Access Token이 없음을 확인");
        String credentials = new String(Base64.getEncoder().encode((SPOTIFY_CLIENT_ID + ":" + SPOTIFY_CLIENT_SECRET).getBytes(StandardCharsets.UTF_8)));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + credentials);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            logger.info("스포티파이에 Access Token 요청 시작");
            response = restTemplate.exchange("https://accounts.spotify.com/api/token", HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                logger.warn("Spotify에서 Access Token 요청 인증 실패. 잘못된 클라이언트 ID/시크릿을 사용했는지 확인하세요.", e);
            } else {
                logger.error("HTTP 클라이언트 오류 발생: 상태 코드 {}", e.getStatusCode(), e);
            }
        } catch (HttpServerErrorException e) {
            logger.error("HTTP 서버 오류 발생: 상태 코드 {}", e.getStatusCode(), e);
        } catch (RestClientException e) {
            logger.error("API 호출 중 오류 발생", e);
        }

        if (response != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                accessToken = jsonNode.get("access_token").asText();
                logger.info("Access Token 성공적으로 받아옴.");

                redisUtil.saveAccessToken(accessToken);
                logger.info("Access Token 성공적으로 인메모리화.");

            } catch (Exception e) {
                logger.error("Access Token 응답 파싱 또는 Redis 저장 중 오류 발생", e);
            }
        }

        if (accessToken == null) {
            logger.warn("Access Token을 얻지 못함. 다음 단계를 진행하기 전에 확인 필요.");
        }

        return accessToken;
    }
}



