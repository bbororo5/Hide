package com.example.backend.user.service;

import com.example.backend.StatusResponseDto;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.entity.EmailMessage;
import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.User;
import com.example.backend.user.entity.UserRoleEnum;
import com.example.backend.user.repository.FollowRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.JwtUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FollowRepository followRepository;
    private final JwtUtil jwtUtil;
    private JavaMailSender javaMailSender;

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
        Follow follow = followRepository.findByFollowerAndFollowing(follower, following).orElse(null);
        if (userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("자신을 팔로우 할 수 없습니다.");
        }
        if (follow == null) {
            Follow newFollow = new Follow(follower, following);
            followRepository.save(newFollow);
            return new ResponseEntity<>(new StatusResponseDto("팔로우 하였습니다."), HttpStatus.OK);
        } else {
            followRepository.delete(follow);
            return new ResponseEntity<>(new StatusResponseDto("팔로우가 취소되었습니다."), HttpStatus.OK);
        }
    }

    public ResponseEntity<StatusResponseDto> sendEmail(UserInfoDto email) {
        if(userRepository.findByEmail(email.getEmail()).isEmpty()) {
            return new ResponseEntity<>(new StatusResponseDto("회원이 존재하지 않습니다."), HttpStatus.CONFLICT);
        }

        User user = userRepository.findByEmail(email.getEmail())
                    .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
        String token = jwtUtil.createTemporalToken(user.getEmail(),user.getRole());

        String subject = "하이드(HIDE) 비밀번호 재설정 요청";
        String resetLink = "http://localhost:3000/changepw?token="+token;
        String message = "비밀번호를 재설정 하시려면 링크를 클릭하세요: " + resetLink;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        try {
            javaMailSender.send(mailMessage);
            return new ResponseEntity<>(new StatusResponseDto("이메일 전송 완료"), HttpStatus.OK);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }

    }
}
