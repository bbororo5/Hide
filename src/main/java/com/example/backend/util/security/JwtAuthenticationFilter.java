package com.example.backend.util.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.dto.UserResponseDto;
import com.example.backend.user.entity.RefreshToken;
import com.example.backend.user.entity.UserRoleEnum;
import com.example.backend.user.repository.RefreshTokenRepository;
import com.example.backend.util.ImageUtil;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.globalDto.StatusResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final JwtUtil jwtUtil;
	private final ImageUtil imageUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository,
		ImageUtil imageUtil) {
		this.jwtUtil = jwtUtil;
		this.imageUtil = imageUtil;
		this.refreshTokenRepository = refreshTokenRepository;
		setFilterProcessesUrl("/api/users/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		try {
			UserInfoDto userInfoDto = new ObjectMapper().readValue(request.getInputStream(),
				UserInfoDto.class);
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				userInfoDto.getEmail(), userInfoDto.getPassword());
			return getAuthenticationManager().authenticate(authenticationToken);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException,
		ServletException {
		String email = ((UserDetailsImpl)authResult.getPrincipal()).getUsername(); //getUsername이지만 email을 받아옴.
		String nickname = ((UserDetailsImpl)authResult.getPrincipal()).getUser().getNickname();
		Long userId = ((UserDetailsImpl)authResult.getPrincipal()).getUser().getUserId();
		UserRoleEnum role = ((UserDetailsImpl)authResult.getPrincipal()).getUser().getRole();

		String createAccessToken = jwtUtil.createAccessToken(email, userId, nickname, role);
		String createRefreshToken = jwtUtil.createRefreshToken(email);

		RefreshToken CheckRefreshToken = refreshTokenRepository.findByKeyEmail(email).orElse(null);
		//해당 email에 대한 refresh 토큰이 있으면 삭제 후 저장.
		if (CheckRefreshToken != null) {
			refreshTokenRepository.delete(CheckRefreshToken);
		}
		RefreshToken newRefreshToken = new RefreshToken(
			jwtUtil.encryptRefreshToken(jwtUtil.substringToken(createRefreshToken)), email);
		refreshTokenRepository.save(newRefreshToken);

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createAccessToken);
		response.addHeader(JwtUtil.REFRESH_HEADER, createRefreshToken);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter()
			.write(new ObjectMapper().writeValueAsString(new UserResponseDto("로그인이 완료되었습니다.", true,
				imageUtil.getImageUrlFromUser(((UserDetailsImpl)authResult.getPrincipal()).getUser()))));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		String errorMsg = "로그인이 실패했습니다.";
		response.setStatus(401);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(new StatusResponseDto(errorMsg)));
	}
}
