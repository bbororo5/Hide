package com.example.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserId(Long userId);

	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

	Optional<User> findByKakaoId(Long kakaoId);

	Optional<User> findByGoogleId(Long googleId);
}
