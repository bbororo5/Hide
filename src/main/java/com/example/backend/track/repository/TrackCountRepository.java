package com.example.backend.track.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amazonaws.services.s3.transfer.Copy;
import com.example.backend.playlist.entity.QPlaylist;
import com.example.backend.track.dto.Track;
import com.example.backend.track.entity.QStar;
import com.example.backend.track.entity.TrackCount;
import com.example.backend.user.entity.QUser;
import com.example.backend.user.entity.User;
import com.querydsl.jpa.impl.JPAQuery;

public interface TrackCountRepository extends JpaRepository<TrackCount, String> , TrackCountRepositoryCustom {
	Optional<TrackCount> findFirstByOrderByCreatedAtAsc();
	// List<TrackCount> findTop2ByUserOrderByPlayCountDesc(User user);
	Optional<TrackCount> findByTrackId(String trackId);
}

