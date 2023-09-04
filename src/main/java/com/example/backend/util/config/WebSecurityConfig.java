package com.example.backend.util.config;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

import com.example.backend.util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.backend.user.repository.RefreshTokenRepository;
import com.example.backend.util.ImageUtil;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.security.JwtAuthenticationFilter;
import com.example.backend.util.security.JwtAuthorizationFilter;
import com.example.backend.util.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final UserDetailsServiceImpl userDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final ImageUtil imageUtil;
	private final RedisUtil redisUtil;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, refreshTokenRepository, imageUtil ,redisUtil);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		return filter;
	}

	@Bean
	public JwtAuthorizationFilter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())
			.sessionManagement(
				sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(STATELESS))
			.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class)
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(
				request -> request
					.requestMatchers("/api/tracks/recommend").authenticated()
					.requestMatchers(DELETE,"/api/users").authenticated()
					.requestMatchers("/api/users/update-profile").authenticated()
					.requestMatchers("/api/token/refresh").authenticated()
					.anyRequest().permitAll()
			);
		return http.build();
	}
}
