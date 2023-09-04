package com.example.backend.userTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.user.dto.UserInfoDto;
import com.example.backend.user.dto.UserProfileDto;
import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.FollowRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.user.service.UserService;
import com.example.backend.util.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private FollowRepository followRepository;

	@InjectMocks
	private UserService userService;

	@Test
	@DisplayName("유저 정보 가져오기 테스트")
	public void testGetUserInfo() {
		// Given
		User mockUser = new User("asdf2222@email.com", "password", "nickname", UserRoleEnum.USER);
		Long mockUserId = 1L;
		when(userRepository.findByUserId(mockUserId)).thenReturn(Optional.of(mockUser));

		// When
		ResponseEntity<UserProfileDto> responseEntity = userService.getUserInfo(mockUserId);

		// Then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(mockUser.getNickname(), responseEntity.getBody().getNickname());
	}

	@Test
	public void testGetToUsers() throws NoSuchFieldException, IllegalAccessException {
		// Given
		User fromUser = new User("email@test.com", "password", "nickname", UserRoleEnum.USER);
		Long fromUserId = 1L;
		User toUser1 = new User("email1@test.com", "password", "nickname1", UserRoleEnum.USER);
		User toUser2 = new User("email2@test.com", "password", "nickname2", UserRoleEnum.USER);

		Follow follow1 = new Follow(fromUser, toUser1);
		Follow follow2 = new Follow(fromUser, toUser2);
		Field field = Follow.class.getSuperclass().getDeclaredField("createdAt");
		field.setAccessible(true);
		field.set(follow1, LocalDateTime.now());
		field.set(follow2, LocalDateTime.now().plusSeconds(2));
		List<Follow> mockFollowList = Arrays.asList(follow1, follow2);

		when(followRepository.findAllByFromUserIdWithUsers(fromUserId)).thenReturn(mockFollowList);

		// When
		List<UserInfoDto> userInfoDtos = userService.getToUsers(fromUserId);

		// Then
		assertEquals(2, userInfoDtos.size());
		assertEquals("nickname2", userInfoDtos.get(0).getNickname());
		assertEquals("nickname1", userInfoDtos.get(1).getNickname());
	}
}