// package com.example.backend.userTest;
//
// import java.util.Optional;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
//
// import com.example.backend.user.dto.SignupRequestDto;
// import com.example.backend.user.repository.UserRepository;
// import com.example.backend.user.service.UserService;
//
// @SpringBootTest
// class UserServiceTest {
//
// 	@Autowired
// 	private UserService userService;
//
// 	@MockBean
// 	private UserRepository userRepository;
//
// 	@Test
// 	void testSignup() {
// 		// given
// 		SignupRequestDto signupRequestDto = new SignupRequestDto();
// 		signupRequestDto.setEmail("test@example.com");
// 		signupRequestDto.setPassword("password");
// 		signupRequestDto.setNickname("nickname");
//
// 		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
// 		// when
// 		ResponseEntity<String> response = userService.signup(signupRequestDto);
//
// 		// then
// 		assertEquals(HttpStatus.CREATED, response.getStatusCode());
// 		assertEquals("회원가입 성공", response.getBody());
// 	}
// }