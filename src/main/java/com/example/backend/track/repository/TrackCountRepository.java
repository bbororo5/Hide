package com.example.backend.track.repository;

import com.example.backend.track.entity.TrackCount;
import com.example.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackCountRepository extends JpaRepository<TrackCount, String> {
    Page<TrackCount> findAll(Pageable pageable);
    List<TrackCount> findTop2ByUserOrderByPlayCountDesc(User user);
}

