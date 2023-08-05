package com.example.backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.user.entity.Follow;
import com.example.backend.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow,Long> {
	Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
	List<Follow> findAllByFromUser(User fromUser);
	List<Follow> findAllByToUser(User toUser);
}
