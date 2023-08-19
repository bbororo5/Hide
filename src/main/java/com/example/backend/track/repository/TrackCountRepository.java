package com.example.backend.track.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.track.entity.TrackCount;
import com.example.backend.user.entity.User;

public interface TrackCountRepository extends JpaRepository<TrackCount, String> {
	Optional<TrackCount> findFirstByOrderByCreatedAtAsc();

	List<TrackCount> findTop2ByUserOrderByPlayCountDesc(User user);

	Optional<TrackCount> findByTrackId(String trackId);
}

