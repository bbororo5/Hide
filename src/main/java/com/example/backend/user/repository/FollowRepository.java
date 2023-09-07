package com.example.backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	@Query("SELECT f FROM Follow f JOIN FETCH f.fromUser JOIN FETCH f.toUser WHERE f.fromUser.userId = :userId")
	List<Follow> findAllByFromUserIdWithUsers(Long userId);
	@Query("SELECT f FROM Follow f JOIN FETCH f.fromUser JOIN FETCH f.toUser WHERE f.toUser.userId = :userId")
	List<Follow> findAllByToUserIdWithUsers(Long userId);

	Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
}
