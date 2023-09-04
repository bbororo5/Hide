package com.example.backend.util;

import com.example.backend.util.execption.TokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;
    public static final String ACCESS_TOKEN_KEY = "accessToken";

    public void saveAccessToken(String accessToken) {
        redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, Duration.ofHours(1)); // 1시간 후 만료
    }

    public String getAccessToken(String accessToken) {
        String retrievedToken = (String) redisTemplate.opsForValue().get(accessToken);
        if (retrievedToken == null) {
            throw new TokenNotFoundException("Redis로부터 토큰을 불러올 수 없습니다.");
        }
        return retrievedToken;
    }

    public void saveRefreshToken(String email, String encryptedRefreshToken) {
        redisTemplate.opsForValue().set(email, encryptedRefreshToken, Duration.ofDays(14)); // 14일 후 만료
    }

    public String getRefreshToken(String email) {
        String retrievedToken = (String) redisTemplate.opsForValue().get(email);
        if (retrievedToken == null) {
            throw new TokenNotFoundException("Redis로부터 토큰을 불러올 수 없습니다.");
        }
        return retrievedToken;
    }
}
