package com.example.backend.track.repository;

import com.example.backend.track.entity.TrackCount;
import com.example.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackCountRepository extends JpaRepository<TrackCount, String> {
    Optional<TrackCount> findFirstByOrderByCreatedAtAsc();

    List<TrackCount> findTop2ByUserOrderByPlayCountDesc(User user);

    Optional<TrackCount> findByTrackId(String trackId);
}

