package com.example.backend.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.user.dto.UserResponseDto;
import com.example.backend.util.StatusResponseDto;
import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.dto.UserProfileDto;
import com.example.backend.user.service.UserService;
import com.example.backend.util.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserService userService;

	@PostMapping("/users/signup")
	public ResponseEntity<StatusResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
		logger.info("회원가입, 요청 email: {}", signupRequestDto.getEmail());
		return userService.signup(signupRequestDto);
	}

	@GetMapping("/users/user-info/{user-id}")
	public ResponseEntity<UserProfileDto> getUserInfo(@PathVariable(name = "user-id") Long userId) {
		logger.info("유저 정보 가져오기");
		return userService.getUserInfo(userId);
	}

	@PatchMapping("/users/update-profile")
	public ResponseEntity<UserResponseDto> updateUser(
		@RequestPart(value = "image", required = false) MultipartFile imageFile,
		@RequestPart(value = "nickname", required = false) String nickname,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("유저 정보 수정");
		return userService.updateUser(imageFile, nickname, userDetails);
	}

	@DeleteMapping("/users")
	public ResponseEntity<StatusResponseDto> removeUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("회원 탈퇴");
		return userService.removeUser(userDetails);
	}

	//	변경 요청 api (본인 email담아서) -> 이메일로 비번변경 페이지 + url 마지막 부분에 토큰 -> 변경할 비밀번호 입력 후 변경 요청 (변경 api) -> 서버에서 토큰 검증 후 비밀번호 변경
	@PostMapping("/users/email/reset-password")
	public ResponseEntity<StatusResponseDto> sendEmail(@RequestBody UserInfoDto userInfoDto) {
		logger.info("비밀번호 변경을 위한 이메일 전송. 요청 email: {}", userInfoDto.getEmail());
		return userService.sendEmail(userInfoDto);
	}

	@PatchMapping("/users/reset-password")
	public ResponseEntity<StatusResponseDto> changePw(@RequestBody UserInfoDto userInfo,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("비밀번호 변경");
		return userService.changePw(userInfo, userDetails);
	}

	@PostMapping("/follow/users/{user-id}")
	public ResponseEntity<StatusResponseDto> followUser(@PathVariable("user-id") Long userId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("팔로우");
		return userService.followUser(userId, userDetails);
	}

	//user가 팔로우 하는 팔로잉 목록
	@GetMapping("/following/users/{user-id}")
	public List<UserInfoDto> getToUsers(@PathVariable("user-id") Long userId) {
		logger.info("해당 유저의 팔로잉 조회");
		return userService.getToUsers(userId);
	}

	//user를 팔로우 하는 팔로워 목록
	@GetMapping("/follower/users/{user-id}")
	public List<UserInfoDto> getFromUsers(@PathVariable("user-id") Long userId) {
		logger.info("해당 유저의 팔로워 조회");
		return userService.getFromUsers(userId);
	}

	@PostMapping("/token/refresh")
	public ResponseEntity<StatusResponseDto> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken,
		HttpServletResponse response) {
		logger.info("ACCESS TOKEN 갱신");
		return userService.refreshAccessToken(refreshToken, response);
	}
}
