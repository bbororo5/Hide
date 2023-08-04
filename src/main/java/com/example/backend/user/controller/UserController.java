package com.example.backend.user.controller;

import com.example.backend.StatusResponseDto;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.user.dto.PasswordDto;
import com.example.backend.user.dto.SignupRequestDto;
import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @PostMapping("/users/signup")
    public ResponseEntity<StatusResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    @DeleteMapping("/users")
    public ResponseEntity<StatusResponseDto> removeUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.removeUser(userDetails);
    }

    //	변경 요청 api (본인 email담아서) -> 이메일로 비번변경 페이지 + url 마지막 부분에 토큰 -> 변경할 비밀번호 입력 후 변경 요청 (변경 api) -> 서버에서 토큰 검증 후 비밀번호 변경
    @PostMapping("/users/email/reset-password")
    public ResponseEntity<StatusResponseDto> sendEmail(@RequestBody UserInfoDto email) {
        return userService.sendEmail(email);
    }

    @PatchMapping("/users/reset-password")
    public ResponseEntity<StatusResponseDto> changePw(@RequestBody PasswordDto passwordDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.changePw(passwordDto, userDetails);
    }

    @PostMapping("/follow/users/{userId}")
    public ResponseEntity<StatusResponseDto> followUser(@PathVariable Long userId,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.followUser(userId, userDetails);
    }
}
