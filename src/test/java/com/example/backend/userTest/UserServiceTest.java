// package com.example.backend.userTest;
//
// import java.util.Optional;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.when;
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// import com.example.backend.user.dto.UserProfileDto;
// import com.example.backend.user.entity.User;
// import com.example.backend.util.UserRoleEnum;
// import com.example.backend.user.repository.FollowRepository;
// import com.example.backend.user.repository.UserRepository;
// import com.example.backend.user.service.UserService;
//
// @ExtendWith(MockitoExtension.class)
// class UserServiceTest {
//
// 	@Mock
// 	private UserRepository userRepository;
// 	@Mock
// 	private FollowRepository followRepository;
//
// 	@InjectMocks
// 	private UserService userService;
//
// 	@Test
// 	@DisplayName("유저 정보 가져오기 테스트")
// 	public void testGetUserInfo() {
// 		// Given
// 		User mockUser = new User("asdf2222@email.com", "password", "nickname", UserRoleEnum.USER);
// 		Long mockUserId = 1L;
// 		when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
// 		when(followRepository.findByFromUserAndToUser(any(), any())).thenReturn(Optional.empty());
//
// 		// When
// 		ResponseEntity<UserProfileDto> responseEntity = userService.getUserInfo(mockUserId);
//
// 		// Then
// 		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 		assertEquals(mockUser.getNickname(), responseEntity.getBody().getNickname());
// 		// 여기에 더 많은 검증 로직을 추가할 수 있어.
// 	}
// }