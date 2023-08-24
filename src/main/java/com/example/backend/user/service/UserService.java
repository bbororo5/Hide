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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.dto.UserProfileDto;
import com.example.backend.user.dto.UserResponseDto;
import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.Image;
import com.example.backend.user.entity.RefreshToken;
import com.example.backend.user.entity.User;
import com.example.backend.user.entity.UserRoleEnum;
import com.example.backend.user.repository.FollowRepository;
import com.example.backend.user.repository.ImageRepository;
import com.example.backend.user.repository.RefreshTokenRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.ImageUtil;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.StatusResponseDto;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final FollowRepository followRepository;
	private final ImageRepository imageRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtil jwtUtil;
	private final ImageUtil imageUtil;
	private final JavaMailSender javaMailSender;
	private final AmazonS3 amazonS3;
	private final String bucket;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	public ResponseEntity<StatusResponseDto> signup(SignupRequestDto signupRequestDto) {
		log.info("회원가입 시작");
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
		log.info("회원가입 완료");
		return new ResponseEntity<>(new StatusResponseDto("회원가입 성공", true), HttpStatus.CREATED);

	}

	@Transactional
	public ResponseEntity<UserResponseDto> updateUser(MultipartFile imageFile, String nickname,
		UserDetailsImpl userDetails) {
		log.info("유저 정보 수정 시작");
		User user = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));

		if (imageFile != null) {
			imageUtil.validateFile(imageFile);
			//기존 이미지를 bucket과 Image 테이블에서 삭제.
			if (user.getImage() != null) {
				DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket,
					user.getImage().getImageKey());
				amazonS3.deleteObject(deleteObjectRequest);
				imageRepository.delete(user.getImage());
			}
			//새로운 image 객체 생성.
			String fileUUID = imageUtil.uploadToS3(imageFile, amazonS3, bucket);
			Image profileImage = new Image(fileUUID, amazonS3.getUrl(bucket, fileUUID).toString());
			user.updateUserImage(profileImage);
		}
		if (nickname != null) {
			user.updateUserNickname(nickname);
		}
		log.info("유저 정보 수정 완료");
		return new ResponseEntity<>(new UserResponseDto("프로필 수정이 완료되었습니다.", true, user.getImage().getImageUrl()),
			HttpStatus.ACCEPTED);
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> removeUser(UserDetailsImpl userDetails) {
		log.info("회원 탈퇴 시작");
		User deleteUser = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
		userRepository.delete(deleteUser);
		log.info("회원 탈퇴 종료");
		return new ResponseEntity<>(new StatusResponseDto("회원탈퇴가 완료되었습니다.", true), HttpStatus.ACCEPTED);
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> followUser(Long userId, UserDetailsImpl userDetails) {
		log.info("유저 팔로우 시작");
		User toUser = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
		User fromUser = userDetails.getUser();
		Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser).orElse(null);
		if (userId.equals(userDetails.getUser().getUserId())) {
			throw new IllegalArgumentException("자신을 팔로우 할 수 없습니다.");
		}
		if (follow == null) {
			Follow newFollow = new Follow(fromUser, toUser);
			followRepository.save(newFollow);
			log.info("유저 팔로우 완료");
			return new ResponseEntity<>(new StatusResponseDto("팔로우 하였습니다.", true), HttpStatus.OK);
		} else {
			followRepository.delete(follow);
			log.info("유저 팔로우 취소");
			return new ResponseEntity<>(new StatusResponseDto("팔로우가 취소되었습니다.", true), HttpStatus.OK);
		}
	}

	public ResponseEntity<StatusResponseDto> sendEmail(UserInfoDto userInfoDto) {
		log.info("비밀번호 변경 이메일 요청 시작");
		if (userRepository.findByEmail(userInfoDto.getEmail()).isEmpty()) {
			return new ResponseEntity<>(new StatusResponseDto("회원이 존재하지 않습니다."), HttpStatus.CONFLICT);
		}
		User user = userRepository.findByEmail(userInfoDto.getEmail())
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
		String token = jwtUtil.createTemporalToken(user.getEmail(), user.getRole());

		String subject = "하이드(HIDE) 비밀번호 재설정 요청";
		String resetLink = "http://localhost:3000/changepw?token=" + token;
		String message = "제한시간은 5분입니다.\n비밀번호를 재설정 하시려면 링크를 클릭하세요\n\n " + resetLink;

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(userInfoDto.getEmail());
		mailMessage.setSubject(subject);
		mailMessage.setText(message);

		try {
			javaMailSender.send(mailMessage);
			log.info("비밀번호 변경 이메일 요청 완료");
			return new ResponseEntity<>(new StatusResponseDto("이메일 전송 완료.", true), HttpStatus.OK);
		} catch (MailException e) {
			log.info("비밀번호 변경 이메일 요청 에러");
			throw new RuntimeException(e);
		}
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> changePw(UserInfoDto userInfo, UserDetailsImpl userDetails) {
		log.info("비밀번호 변경 시작");
		User user = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
		String newPassword = passwordEncoder.encode(userInfo.getPassword());
		user.updatePassword(newPassword);
		log.info("비밀번호 변경 완료");
		return new ResponseEntity<>(new StatusResponseDto("비밀번호가 변경되었습니다.", true), HttpStatus.OK);
	}

	@Transactional
	public List<UserInfoDto> getToUsers(Long userId) {
		log.info("팔로잉 목록 조회 시작");
		List<Follow> followingList = followRepository.findAllByFromUserIdWithUsers(userId);
		Collections.sort(followingList, Comparator.comparing(Follow::getCreatedAt).reversed());
		List<UserInfoDto> userResponseDtoList = new ArrayList<>();
		for (Follow follow : followingList) {
			userResponseDtoList.add(new UserInfoDto(follow.getToUser()));
		}
		log.info("팔로잉 목록 조회 완료");
		return userResponseDtoList;
	}

	@Transactional
	public List<UserInfoDto> getFromUsers(Long userId) {
		log.info("팔로워 목록 조회 시작");
		User toUser = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
		List<Follow> followingList = followRepository.findAllByToUser(toUser);
		Collections.sort(followingList, Comparator.comparing(Follow::getCreatedAt).reversed());
		List<UserInfoDto> userResponseDtoList = new ArrayList<>();
		for (Follow follow : followingList) {
			userResponseDtoList.add(new UserInfoDto(follow.getFromUser()));
		}
		log.info("팔로워 목록 조회 완료");
		return userResponseDtoList;
	}

	public ResponseEntity<StatusResponseDto> refreshAccessToken(String refreshToken, HttpServletResponse response) {
		log.info("엑세스 토큰 갱신 시작");
		String token = jwtUtil.substringToken(refreshToken);
		String email = jwtUtil.getUserInfoFromToken(token).getSubject();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));
		String newAccessToken = jwtUtil.createAccessToken(email, user.getUserId(), user.getNickname(), user.getRole());
		RefreshToken refreshTokenFromDB = refreshTokenRepository.findByKeyEmail(email)
			.orElseThrow(() -> new NoSuchElementException("리프레시 토큰이 없습니다."));
		if (jwtUtil.encryptRefreshToken(token).equals(refreshTokenFromDB.getRefreshToken())) {
			response.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
			log.info("엑세스 토큰 갱신 완료");
			return new ResponseEntity<>(new StatusResponseDto("새로운 엑세스 토큰이 발급되었습니다.", true), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new StatusResponseDto("리프레시 토큰이 유효하지 않습니다.", false), HttpStatus.CONFLICT);
		}
	}

	public ResponseEntity<UserProfileDto> getUserInfo(Long userId) {
		log.info("유저 정보 가져오기 시작");
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));
		boolean isFollowing = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
			&& authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
			User follower = userDetails.getUser();
			Follow follow = followRepository.findByFromUserAndToUser(follower, user).orElse(null);
			if (follow != null) {
				isFollowing = true;
			}
		}
		log.info("유저 정보 가져오기 완료");
		return new ResponseEntity<>(new UserProfileDto(user, isFollowing), HttpStatus.OK);
	}

}
