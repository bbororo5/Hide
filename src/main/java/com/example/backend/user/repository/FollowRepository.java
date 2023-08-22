package com.example.backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	@Query("SELECT f FROM Follow f JOIN FETCH f.fromUser JOIN FETCH f.toUser WHERE f.fromUser.userId = :userId")
	List<Follow> findAllByFromUserIdWithUsers(Long userId);
	Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);

	List<Follow> findAllByFromUser(User fromUser);

	List<Follow> findAllByToUser(User toUser);
}
