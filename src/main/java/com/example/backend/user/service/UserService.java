package com.example.backend.user.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.example.backend.StatusResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.security.UserDetailsImpl;
import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.User;
import com.example.backend.user.entity.UserRoleEnum;
import com.example.backend.user.repository.FollowRepository;
import com.example.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final FollowRepository followRepository;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	public ResponseEntity<StatusResponseDto> signup(SignupRequestDto signupRequestDto) {
		String email = signupRequestDto.getEmail();
		String password = passwordEncoder.encode(signupRequestDto.getPassword());
		String nickname = signupRequestDto.getNickname();
		Optional<User> findUser = userRepository.findByEmail(email);
		if (findUser.isPresent()) {
			return new ResponseEntity<>(new StatusResponseDto("이미 존재하는 사용자 입니다."), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 사용자 ROLE 확인
		UserRoleEnum role = UserRoleEnum.USER;
		if (signupRequestDto.isAdmin()) {
			if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
				return new ResponseEntity<>(new StatusResponseDto("관리자 암호가 틀려 등록이 불가능합니다."), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			role = UserRoleEnum.ADMIN;
		}

		User user = new User(email, password, nickname, role);

		userRepository.save(user);
		return new ResponseEntity<>(new StatusResponseDto("회원가입 성공"), HttpStatus.CREATED);

	}

	public ResponseEntity<StatusResponseDto> removeUser(UserDetailsImpl userDetails) {
		User deleteUser = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		userRepository.delete(deleteUser);
		return new ResponseEntity<>(new StatusResponseDto("회원탈퇴가 완료되었습니다."), HttpStatus.ACCEPTED);
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> followUser(Long userId, UserDetailsImpl userDetails) {
		User following = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
		User follower = userDetails.getUser();
		Follow follow = followRepository.findByFollowerAndFollowing(follower,following).orElse(null);
		if(userId.equals(userDetails.getUser().getUserId())){
			throw new IllegalArgumentException("자신을 팔로우 할 수 없습니다.");
		}
		if(follow== null){
			Follow newFollow = new Follow(follower,following);
			followRepository.save(newFollow);
			return new ResponseEntity<>(new StatusResponseDto("팔로우 하였습니다."), HttpStatus.OK);
		}else{
			followRepository.delete(follow);
			return new ResponseEntity<>(new StatusResponseDto("팔로우가 취소되었습니다."), HttpStatus.OK);
		}
	}
}
