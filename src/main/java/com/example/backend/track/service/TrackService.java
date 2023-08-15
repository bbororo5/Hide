package com.example.backend.track.service;

import com.example.backend.track.dto.TrackDetailModal;
import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.TrackCountRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.NotFoundTrackException;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.dto.Track;
import com.example.backend.util.spotify.SpotifyUtil;
import com.example.backend.util.youtube.YoutubeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackCountRepository trackCountRepository;
    private final UserRepository userRepository;
    private SpotifyUtil spotifyUtil;
    private YoutubeUtil youtubeUtil;




    public void increasePlayCount(String trackId, User user) {
        TrackCount trackCount = trackCountRepository.findById(trackId)
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

    public List<Track> getTopTracks() {
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

        if (trackIds.isEmpty()) {
            throw new RuntimeException("추천 트랙을 받아오지 못했습니다");
        }

        try {
            return spotifyUtil.getRecommendTracks(trackIds);
        } catch (NotFoundTrackException e) {
            throw new NotFoundTrackException("트랙을 찾을 수 없습니다.");
        }
    }

    public TrackDetailModal getTrackDetail(String trackId) {
        Track track = spotifyUtil.getTracksInfo(trackId);
        String artistName = track.getArtists().get(0).getArtistName();
        String trackTitle = track.getTrackTitle();
        String videoId = youtubeUtil.getVideoId(artistName + " " + trackTitle);

        return TrackDetailModal.builder()
                .image(track.getAlbum640Image())
                .album(track.getAlbumName())
                .artist(artistName)
                .title(trackTitle)
                .yUrl("https://www.youtube.com/watch?v=" + videoId)
                .build();

    }

    public List<Track> getRecentTracks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));
        List<String> trackIds = recentRepository.findTrackIdByUserOrderByCreationDateDesc(user);
        return spotifyUtil.getTracksInfo(trackIds);
    }
}