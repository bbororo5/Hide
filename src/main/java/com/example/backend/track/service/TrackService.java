package com.example.backend.track.service;

import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.TrackCountRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.NotFoundTrackException;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.dto.Track;
import com.example.backend.util.spotify.SpotifyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackCountRepository trackCountRepository;
    private final UserRepository userRepository;
    private SpotifyUtil spotifyUtil;

    public void increasePlayCount(String trackId, User user) {
        TrackCount trackCount = trackCountRepository.findById(trackId)
                .orElse(new TrackCount(trackId, user, 1)); // 트랙이 없는 경우 새 TrackCount 생성
        trackCount.increasePlayCount();
        trackCountRepository.save(trackCount);
    }

    public List<Track> getTracksOrderedByPlayCount() {
        Pageable topTen = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "playCount"));
        List<TrackCount> trackCounts = (List<TrackCount>) trackCountRepository.findAll(topTen).getContent();

        List<String> trackIds = new ArrayList<>();
        for (TrackCount trackCount : trackCounts) {
            trackIds.add(trackCount.getTrackId());
        }

        return spotifyUtil.getTracksInfo(trackIds);
    }

    public List<String> getTop2TracksByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        List<TrackCount> trackCounts = trackCountRepository.findTop2ByUserOrderByPlayCountDesc(user);

        List<String> trackIds = new ArrayList<>();
        for (TrackCount trackCount : trackCounts) {
            trackIds.add(trackCount.getTrackId());
        }

        return trackIds;
    }

    public List<Track> recommendTracks(UserDetailsImpl userDetails) {
        List<String> trackIds = getTop2TracksByUser(userDetails.getUser().getUserId());
        for (String trackId : trackIds) {
            try {
                return spotifyUtil.getRecommendTracks(trackId);
            } catch (NotFoundTrackException e) {
                throw new NotFoundTrackException("트랙을 찾을 수 없습니다.");
            }

        }
        throw new RuntimeException("추천 트랙을 받아오지 못했습니다");
    }
}