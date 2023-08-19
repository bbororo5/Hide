package com.example.backend.track.service;

import com.example.backend.track.dto.Top7Dto;
import com.example.backend.track.dto.TrackDetailDto;
import com.example.backend.track.dto.TrackDetailModal;
import com.example.backend.track.entity.Recent;
import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.TrackCountRepository;
import com.example.backend.user.entity.User;
import com.example.backend.track.repository.RecentRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.NotFoundTrackException;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.track.dto.Track;
import com.example.backend.util.spotify.SpotifyUtil;
import com.example.backend.util.youtube.YoutubeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackCountRepository trackCountRepository;
    private final UserRepository userRepository;
    private final SpotifyUtil spotifyUtil;
    private final YoutubeUtil youtubeUtil;
    private final RecentRepository recentRepository;

    public void increasePlayCount(String trackId, User user) {
        TrackCount trackCount = trackCountRepository.findByTrackId(trackId)
                .orElse(new TrackCount(trackId, user, 0)); // 트랙이 없는 경우 새 TrackCount 생성
        handleTrackCountLimit();
        trackCount.increasePlayCount();
        trackCountRepository.save(trackCount);
    }

    private void handleTrackCountLimit() {
        long count = trackCountRepository.count();
        if (count >= 500) {
            removeOldestTrackCount();
        }
    }

    private void removeOldestTrackCount() {
        trackCountRepository.findFirstByOrderByCreatedAtAsc().ifPresent(trackCountRepository::delete);
    }

    public List<Track> getTopTracksByAllUser() {
        Pageable top10 = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "playCount"));
        List<TrackCount> trackCounts = trackCountRepository.findAll(top10).getContent();

        List<String> trackIds = trackCounts.stream()
                .map(TrackCount::getTrackId)
                .collect(Collectors.toList());

        return spotifyUtil.getTracksInfo(trackIds);
    }

    private List<String> getTop2TracksByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("유저 " +userId + "를 찾을 수 없습니다."));

        List<TrackCount> trackCounts = trackCountRepository.findTop2ByUserOrderByPlayCountDesc(user);

        List<String> trackIds = new ArrayList<>();
        for (TrackCount trackCount : trackCounts) {
            trackIds.add(trackCount.getTrackId());
        }

        // 초기 사용자 체크: trackCounts가 비어있거나 크기가 2보다 작을 경우 인기곡 로직 추가 필요

        return trackIds;
    }

    public List<Track> recommendTracks(UserDetailsImpl userDetails) {
        List<String> trackIds = getTop2TracksByUser(userDetails.getUser().getUserId());

        if (trackIds.isEmpty() || trackIds.size() < 2) {
            return getRecommendTracksForNewUsers();
        }

        try {
            return spotifyUtil.getRecommendTracks(trackIds);
        } catch (NotFoundTrackException e) {
            throw new NotFoundTrackException("트랙을 찾을 수 없습니다.");
        }
    }


    private List<Track> getRecommendTracksForNewUsers() {
        List<String> trackIds = new ArrayList<>();
        trackIds.add("7iN1s7xHE4ifF5povM6A48");
        trackIds.add("58dSdjfEYNSxte1aNVxuNf");

        return spotifyUtil.getRecommendTracks(trackIds);
    }

    public TrackDetailModal getTrackDetailModal(String trackId) {
        Track track = spotifyUtil.getTracksInfo(trackId);
        String artistName = track.getArtists().get(0).getArtistName();
        String trackTitle = track.getTitle();
        String videoId = youtubeUtil.getVideoId(artistName + " " + trackTitle);

        return TrackDetailModal.builder()
                .image(track.getImage())
                .album(track.getAlbum())
                .artist(artistName)
                .title(trackTitle)
                .yUrl("https://www.youtube.com/watch?v=" + videoId)
                .build();
    }

    public TrackDetailDto getTrackDetail(String trackId) {
        Track track =spotifyUtil.getTracksInfo(trackId);
        TrackDetailDto trackDetailDto = new TrackDetailDto(track);
        return trackDetailDto;
    }

    public List<Track> getRecentTracks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));
        List<Recent> recentList = recentRepository.findAllByUserOrderByCreationDateDesc(user);
        List<String> trackIds = recentList.stream()
            .map(Recent::getTrackId)
            .toList();
        return spotifyUtil.getTracksInfo(trackIds);
    }

    @Transactional
    public void createRecentTrack(String trackId, User user) {
        List<Recent> recentTracks = recentRepository.findByUserOrderByCreationDateAsc(user);
        if (recentTracks.size() >= 20) {
            // 가장 오래된 곡 제거
            recentRepository.delete(recentTracks.get(0));
        }
        Recent recent = new Recent(trackId, user);
        recentRepository.save(recent);
    }

    public List<Top7Dto> get7RecentTracks() {
        Pageable topSeven = PageRequest.of(0, 7);
        List<Recent> recent7tracts = recentRepository.findTop7ByOrderByCreationDateDesc(topSeven);
        List<String> trackIds = recent7tracts.stream()
            .map(Recent::getTrackId)
            .toList();
        List<Track> top7TrackList = spotifyUtil.getTracksInfo(trackIds);
        return top7TrackList.stream().map(Top7Dto::new).toList();
    }
}