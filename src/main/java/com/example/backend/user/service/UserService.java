package com.example.backend.user.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.StatusResponseDto;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.dto.UserResponseDto;
import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.User;
import com.example.backend.user.entity.UserRoleEnum;
import com.example.backend.user.repository.FollowRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final FollowRepository followRepository;
	private final JwtUtil jwtUtil;
	private final JavaMailSender javaMailSender;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	public ResponseEntity<StatusResponseDto> signup(SignupRequestDto signupRequestDto) {
		String email = signupRequestDto.getEmail();
		String nickname = signupRequestDto.getNickname();
		String password = passwordEncoder.encode(signupRequestDto.getPassword());
		Optional<User> findEmailUser = userRepository.findByEmail(email);
		if (findEmailUser.isPresent()) {
			return new ResponseEntity<>(new StatusResponseDto("이미 존재하는 이메일 입니다."), HttpStatus.CONFLICT);
		}
		Optional<User> findNicknameUser = userRepository.findByNickname(email);
		if (findNicknameUser.isPresent()) {
			return new ResponseEntity<>(new StatusResponseDto("이미 존재하는 닉네임 입니다."), HttpStatus.CONFLICT);
		}

		// 사용자 ROLE 확인
		UserRoleEnum role = UserRoleEnum.USER;
		if (signupRequestDto.isAdmin()) {
			if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
				return new ResponseEntity<>(new StatusResponseDto("관리자 암호가 틀려 등록이 불가능합니다."),
					HttpStatus.INTERNAL_SERVER_ERROR);
			}
			role = UserRoleEnum.ADMIN;
		}

		User user = new User(email, password, nickname, role);

		userRepository.save(user);
		return new ResponseEntity<>(new StatusResponseDto("회원가입 성공", true), HttpStatus.CREATED);

	}

	public ResponseEntity<StatusResponseDto> removeUser(UserDetailsImpl userDetails) {
		User deleteUser = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		userRepository.delete(deleteUser);
		return new ResponseEntity<>(new StatusResponseDto("회원탈퇴가 완료되었습니다.", true), HttpStatus.ACCEPTED);
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> followUser(Long userId, UserDetailsImpl userDetails) {
		User toUser = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
		User fromUser = userDetails.getUser();
		Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser).orElse(null);
		if (userId.equals(userDetails.getUser().getUserId())) {
			throw new IllegalArgumentException("자신을 팔로우 할 수 없습니다.");
		}
		if (follow == null) {
			Follow newFollow = new Follow(fromUser, toUser);
			followRepository.save(newFollow);
			return new ResponseEntity<>(new StatusResponseDto("팔로우 하였습니다.", true), HttpStatus.OK);
		} else {
			followRepository.delete(follow);
			return new ResponseEntity<>(new StatusResponseDto("팔로우가 취소되었습니다.", true), HttpStatus.OK);
		}
	}

	public ResponseEntity<StatusResponseDto> sendEmail(UserInfoDto email) {
		if (userRepository.findByEmail(email.getEmail()).isEmpty()) {
			return new ResponseEntity<>(new StatusResponseDto("회원이 존재하지 않습니다."), HttpStatus.CONFLICT);
		}
		User user = userRepository.findByEmail(email.getEmail())
			.orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
		String token = jwtUtil.createTemporalToken(user.getEmail(), user.getRole());

		String subject = "하이드(HIDE) 비밀번호 재설정 요청";
		String resetLink = "http://localhost:3000/changepw?token=" + token;
		String message = "제한시간은 5분입니다.\n비밀번호를 재설정 하시려면 링크를 클릭하세요\n\n " + resetLink;

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email.getEmail());
		mailMessage.setSubject(subject);
		mailMessage.setText(message);

		try {
			javaMailSender.send(mailMessage);
			return new ResponseEntity<>(new StatusResponseDto("이메일 전송 완료.", true), HttpStatus.OK);
		} catch (MailException e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> changePw(UserInfoDto userInfo, UserDetailsImpl userDetails,
		HttpServletRequest request) {
		User user = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
		String newPassword = passwordEncoder.encode(userInfo.getPassword());
		if (!jwtUtil.validateToken(request.getHeader(JwtUtil.AUTHORIZATION_HEADER))) {
			return new ResponseEntity<>(new StatusResponseDto("비밀번호 변경이 실패했습니다."), HttpStatus.UNAUTHORIZED);
		}
		user.updatePassword(newPassword);
		return new ResponseEntity<>(new StatusResponseDto("비밀번호가 변경되었습니다.", true), HttpStatus.OK);
	}

	public List<UserResponseDto> getToUsers(Long userId) {
		User fromUser = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
		List<Follow> followingList = followRepository.findAllByFromUser(fromUser);
		Collections.sort(followingList, Comparator.comparing(Follow::getCreatedAt).reversed());
		List<UserResponseDto> userResponseDtoList = new ArrayList<>();
		for (Follow follow : followingList) {
			userResponseDtoList.add(new UserResponseDto(follow.getToUser()));
		}
		return userResponseDtoList;
	}

	public List<UserResponseDto> getFromUsers(Long userId) {
		User toUser = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
		List<Follow> followingList = followRepository.findAllByToUser(toUser);
		Collections.sort(followingList, Comparator.comparing(Follow::getCreatedAt).reversed());
		List<UserResponseDto> userResponseDtoList = new ArrayList<>();
		for (Follow follow : followingList) {
			userResponseDtoList.add(new UserResponseDto(follow.getFromUser()));
		}
		return userResponseDtoList;
	}
}
