package com.example.backend.track.service;

import com.example.backend.playlist.entity.Playlist;
import com.example.backend.playlist.repository.PlayListRepository;
import com.example.backend.track.dto.Track;
import com.example.backend.track.entity.Star;
import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.StarRepository;
import com.example.backend.track.repository.TrackCountRepository;
import com.example.backend.track.repository.TrackCountRepositoryImpl;
import com.example.backend.user.entity.User;
import com.example.backend.util.globalDto.StatusResponseDto;
import com.example.backend.util.security.UserDetailsImpl;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @InjectMocks
    private TrackService trackService;
    @Mock
    private TrackCountRepository trackCountRepository;
    @Mock
    private SpotifyRequestManager spotifyRequestManager;

    @Mock
    private StarRepository starRepository;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private User user;
    @Mock
    private PlayListRepository playListRepository;
    @Mock
    private TrackCountRepositoryImpl trackCountRepositoryImpl;

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

//    @Test
//    @DisplayName("추천 트랙 가져오기 테스트")
//    void testRecommendTracks() {
//        UserDetailsImpl userDetailsMock = mock(UserDetailsImpl.class);
//        User userMock = mock(User.class);
//        when(userDetailsMock.getUser()).thenReturn(userMock);
//
////        when(trackCountRepositoryImpl.findTrackIdsFromFollowing(userMock)).thenReturn(new HashSet<>(Arrays.asList("track1", "track2")));
////        when(trackCountRepositoryImpl.findTrackIdsFromFollower(userMock)).thenReturn(new HashSet<>());
////        when(trackCountRepositoryImpl.findHighRatedAndRelatedTracks(userMock)).thenReturn(new HashSet<>());
////        when(trackCountRepositoryImpl.findRecent5TracksFromUser(userMock)).thenReturn(new HashSet<>());
//
//        Track trackMock = mock(Track.class);
//        when(spotifyRequestManager.getTracksInfo(anyList())).thenReturn(Arrays.asList(trackMock));
//
//        Playlist playlistMock = mock(Playlist.class);
//        when(playlistMock.getTrackId()).thenReturn("playlistTrack1");
//        when(playListRepository.findByUser(userMock)).thenReturn(Arrays.asList(playlistMock));
//        when(spotifyRequestManager.getRecommendTracks(anyList())).thenReturn(Arrays.asList(trackMock));
//
//        List<Track> result = trackService.recommendTracks(userDetailsMock);
//
//        assertNotNull(result);
//        assertEquals(2, result.size()); // Just an example, adjust based on your logic
   // }

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
        // Mocking
        String trackId = "track123";
        Star mockStar = mock(Star.class);

        when(userDetails.getUser()).thenReturn(user);
        when(starRepository.findByUserAndTrackId(user, trackId)).thenReturn(Optional.of(mockStar));

        // 테스트 실행
        ResponseEntity<StatusResponseDto> result = trackService.deleteStarRating(trackId, userDetails);

        // 검증
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(starRepository,times(1)).delete(mockStar);
    }

    @Test
    void getStarList() {
    }
}