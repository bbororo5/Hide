package com.example.backend.util.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.util.UserRoleEnum;
import com.example.backend.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Jwt Authorization")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl serviceImpl;

	public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl serviceImpl) {
		this.jwtUtil = jwtUtil;
		this.serviceImpl = serviceImpl;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException,
		IOException {
		String token = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);
		if (StringUtils.hasText(token)) {
			if (token.startsWith(JwtUtil.BEARER_PREFIX)) {
				token = jwtUtil.substringToken(token);
			}

			if (!jwtUtil.validateToken(token, response)) {
				log.error("유효성 검증에 실패했습니다.");
				return;
			}

			Claims claims = jwtUtil.getUserInfoFromToken(token);
			String email = claims.getSubject();
			if(claims.getExpiration().getTime()-System.currentTimeMillis()<5*60*1000){
				String nickname = (String) claims.get("nickname");
				Long userId = Long.parseLong((String) claims.get("userId"));
				UserRoleEnum role = UserRoleEnum.valueOf((String) claims.get(JwtUtil.AUTHORIZATION_KEY));
				response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessToken(email,userId,nickname,role));
			}
			log.info(email);
			try {
				setAuthentication(email);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		//리프레시 토큰 검증 로직 만들기.
		String refreshToken = request.getHeader(JwtUtil.REFRESH_HEADER);
		if (StringUtils.hasText(refreshToken)) {
			if (refreshToken.startsWith(JwtUtil.BEARER_PREFIX)) {
				refreshToken = jwtUtil.substringToken(refreshToken);
			}

			if (!jwtUtil.validateToken(refreshToken, response)) {
				log.error("유효성 검증에 실패했습니다.");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	public void setAuthentication(String email) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication auth = createAuthentication(email);
		context.setAuthentication(auth);
		SecurityContextHolder.setContext(context);
	}

	private Authentication createAuthentication(String email) {
		UserDetails userDetails = serviceImpl.loadUserByUsername(email);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}
