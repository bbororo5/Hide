package com.example.backend.track.repository;

import java.util.List;

import com.example.backend.user.entity.User;

public interface TrackCountRepositoryCustom {
	List<String> findTrackIdsFromFollowing(User currentUser);
	List<String> findTrackIdsFromFollower(User currentUser);
	List<String> findHighRatedAndRelatedTracks(User currentUser);
	List<String> findRecent5TracksFromUser(User currentUser);
}
