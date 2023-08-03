package com.example.backend.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.security.UserDetailsImpl;
import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.entity.User;
import com.example.backend.user.entity.UserRoleEnum;
import com.example.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	public ResponseEntity<String> signup(SignupRequestDto signupRequestDto) {
		String email = signupRequestDto.getEmail();
		String password = passwordEncoder.encode(signupRequestDto.getPassword());
		String nickname = signupRequestDto.getNickname();
		Optional<User> findUser = userRepository.findByEmail(email);
		if (findUser.isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
		}

		// 사용자 ROLE 확인
		UserRoleEnum role = UserRoleEnum.USER;
		if (signupRequestDto.isAdmin()) {
			if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
				throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
			}
			role = UserRoleEnum.ADMIN;
		}

		User user = new User(email, password, nickname, role);

		userRepository.save(user);
		return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
	}

	public ResponseEntity<String> removeUser(UserDetailsImpl userDetails) {
		User deleteUser = userRepository.findById(userDetails.getUser().getId())
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		userRepository.delete(deleteUser);
		return new ResponseEntity<>("회원 삭제", HttpStatus.ACCEPTED);
	}
}
