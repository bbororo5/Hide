package com.example.backend.track.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.track.entity.Star;
import com.example.backend.user.entity.User;

public interface StarRepository extends JpaRepository<Star,Long> {
	Optional<Star> findByUserAndTrackId(User user, String trackId);

	@Query("SELECT AVG(s.star) FROM Star s WHERE s.trackId = :trackId")
	Double findAverageStarByTrackId(@Param("trackId") String trackId);
}
