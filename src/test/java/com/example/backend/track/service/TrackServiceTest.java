package com.example.backend.track.service;

import com.example.backend.track.dto.Track;
import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.TrackCountRepository;
import com.example.backend.util.spotify.SpotifyRequestManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @InjectMocks
    private TrackService trackService;
    @Mock
    private TrackCountRepository trackCountRepository;
    @Mock
    private SpotifyRequestManager spotifyRequestManager;

    @Nested
    @DisplayName("increasePlayCount 메서드 테스트")
    class IncreasePlayCountTests {
        @Test
        @DisplayName("새로운 트랙의 플레이 카운트 증가 테스트")
        public void testIncreasePlayCount_NewTrack() {
            String trackId = "newTrack";
            when(trackCountRepository.findByTrackId(trackId)).thenReturn(Optional.empty());
            when(trackCountRepository.count()).thenReturn(0L);

            trackService.increasePlayCount(trackId);

            ArgumentCaptor<TrackCount> argumentCaptor = ArgumentCaptor.forClass(TrackCount.class);
            verify(trackCountRepository).save(argumentCaptor.capture());

            assertEquals(1, argumentCaptor.getValue().getPlayCount());
        }

        @Test
        @DisplayName("기존 트랙의 플레이 카운트 증가 테스트")
        public void testIncreasePlayCount_ExistingTrack() {
            String trackId = "existingTrack";
            TrackCount existingTrackCount = new TrackCount(trackId, 5);
            when(trackCountRepository.findByTrackId(trackId)).thenReturn(Optional.of(existingTrackCount));
            when(trackCountRepository.count()).thenReturn(0L);

            trackService.increasePlayCount(trackId);

            assertEquals(6, existingTrackCount.getPlayCount());
        }

        @Test
        @DisplayName("트랙 카운트 레코드 500 제한 테스트")
        public void testHandleTrackCountLimit_RemoveOldest() {
            when(trackCountRepository.count()).thenReturn(500L);
            TrackCount oldestTrackCount = new TrackCount("oldest", 1);
            when(trackCountRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.of(oldestTrackCount));

            trackService.increasePlayCount("anyTrackId");

            verify(trackCountRepository).delete(oldestTrackCount);
        }
    }

    @Test
    @DisplayName("TrackCount 테이블의 Top 10 트랙 가져오기 테스트")
    void getTopTracksByAllUser() {
        List<TrackCount> trackCounts = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String trackId = "trackId " + i;
            int playCount = 11 - i;
            trackCounts.add(new TrackCount(trackId, playCount));
        }

        Pageable top10 = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "playCount"));
        when(trackCountRepository.findAll(top10)).thenReturn(new PageImpl<>(trackCounts));

        List<Track> expectedTracks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Track track = Track.builder()
                    .id("trackId " + i)
                    .title("Track " + i)
                    .album("Album " + i)
                    .image("imageUrl " + i)
                    .artists(Arrays.asList(
                            Track.Artist.builder().artistName("Artist " + (i + 1)).build(),
                            Track.Artist.builder().artistName("Artist " + (i + 2)).build()
                    ))
                    .genre(Arrays.asList(
                            Track.Genre.builder().genre("Genre " + (i + 1)).build(),
                            Track.Genre.builder().genre("Genre " + (i + 2)).build()
                    ))
                    .build();

            expectedTracks.add(track);
        }

        when(spotifyRequestManager.getTracksInfo(anyList())).thenReturn(expectedTracks);

        List<Track> tracks = trackService.getTopTracksByAllUser();

        assertEquals(10, tracks.size());
        for (int i = 0; i < 10; i++) {
            assertEquals("trackId " + (i + 1), tracks.get(i).getTrackId());
        }
    }

    @Test
    void testRecommendTracks() {
    }

    @Test
    void getTrackDetailModal() {
    }

    @Test
    void getTrackDetail() {
    }

    @Test
    void getRecentTracks() {
    }

    @Test
    void createRecentTrack() {
    }

    @Test
    void get7RecentTracks() {
    }

    @Test
    void setStarRating() {
    }

    @Test
    void deleteStarRating() {
    }

    @Test
    void getStarList() {
    }
}