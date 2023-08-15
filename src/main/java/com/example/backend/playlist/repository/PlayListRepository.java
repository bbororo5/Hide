package com.example.backend.playlist.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.playlist.entity.Playlist;
import com.example.backend.user.entity.User;

public interface PlayListRepository extends JpaRepository<Playlist,Long> {
	Optional<Playlist> findByIdAndUser(Long id, User user);
}
