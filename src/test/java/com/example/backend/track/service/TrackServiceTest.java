package com.example.backend.track.service;

import com.example.backend.playlist.repository.PlayListRepository;
import com.example.backend.track.dto.*;
import com.example.backend.track.entity.Recent;
import com.example.backend.track.entity.Star;
import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.RecentRepository;
import com.example.backend.track.repository.StarRepository;
import com.example.backend.track.repository.TrackCountRepository;
import com.example.backend.track.repository.TrackCountRepositoryImpl;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.execption.TrackNotFoundException;
import com.example.backend.util.globalDto.StatusResponseDto;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.SpotifyRequestManager;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @InjectMocks
    private TrackService trackService;
    @Mock
    private TrackCountRepository trackCountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecentRepository recentRepository;
    @Mock
    private SpotifyRequestManager spotifyRequestManager;
    @Mock
    private StarRepository starRepository;
    @Mock
    private UserDetailsImpl userDetails;
    @Mock
    private User user;
    @Mock
    private JPAQueryFactory jpaQueryFactory;
    @Mock
    private PlayListRepository playListRepository;
    @Mock
    private TrackCountRepositoryImpl trackCountRepositoryImpl;
    @Mock
    private JPAQuery<StarListResponseDto> jpaQueryMock;

    @Nested
    @DisplayName("increasePlayCount 메서드 테스트")
    class IncreasePlayCountTests {
        @Test
        @DisplayName("새로운 트랙의 플레이 카운트 증가 테스트")
        public void IncreasePlayCount_whenNewTrack() {
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
        public void IncreasePlayCount_whenExistingTrack() {
            String trackId = "existingTrack";
            TrackCount existingTrackCount = new TrackCount(trackId, 5);
            when(trackCountRepository.findByTrackId(trackId)).thenReturn(Optional.of(existingTrackCount));
            when(trackCountRepository.count()).thenReturn(0L);

            trackService.increasePlayCount(trackId);

            assertEquals(6, existingTrackCount.getPlayCount());
        }

        @Test
        @DisplayName("트랙 카운트 레코드 500 제한 테스트")
        public void IncreasePlayCountTests_whenOverLimit500() {
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
                    // .genre(Arrays.asList(
                    //         Track.Genre.builder().genre("Genre " + (i + 1)).build(),
                    //         Track.Genre.builder().genre("Genre " + (i + 2)).build()
                    // ))
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

    @Nested
    @DisplayName("추천 트랙 가져오기 테스트")
    class testRecommendTracks {
        @Test
        @DisplayName("정상 작동 테스트")
        public void recommendTracks_returnsRecommendedTracks() {
            UserDetailsImpl userDetailsMock = mock(UserDetailsImpl.class);
            User userMock = mock(User.class);
            when(userDetailsMock.getUser()).thenReturn(userMock);

            List<String> mockTrackIds = Arrays.asList("mockId1", "mockId2");
            when(trackCountRepositoryImpl.findTrackIdsFromFollowing(userMock)).thenReturn(mockTrackIds);
            when(trackCountRepositoryImpl.findTrackIdsFromFollower(userMock)).thenReturn(mockTrackIds);
            when(trackCountRepositoryImpl.findHighRatedAndRelatedTracks(userMock)).thenReturn(mockTrackIds);
            when(trackCountRepositoryImpl.findRecent5TracksFromUser(userMock)).thenReturn(mockTrackIds);

            Track trackMock1 = mock(Track.class);
            Track trackMock2 = mock(Track.class);
            when(spotifyRequestManager.getTracksInfo(anyList())).thenReturn(Arrays.asList(trackMock1, trackMock2));

            List<Track> result = trackService.recommendTracks(userDetailsMock);

            assertEquals(2, result.size());
            verify(spotifyRequestManager).getTracksInfo(anyList());
        }

        @Test
        @DisplayName("TrackNotFound 예외 발생 테스트")
        public void recommendTracks_whenTrackNotFoundException() {
            UserDetailsImpl userDetailsMock = mock(UserDetailsImpl.class);
            User userMock = mock(User.class);
            when(userDetailsMock.getUser()).thenReturn(userMock);

            List<String> mockTrackIds = Arrays.asList("mockId1", "mockId2");
            when(trackCountRepositoryImpl.findTrackIdsFromFollowing(userMock)).thenReturn(mockTrackIds);
            when(trackCountRepositoryImpl.findTrackIdsFromFollower(userMock)).thenReturn(mockTrackIds);
            when(spotifyRequestManager.getTracksInfo(anyList())).thenThrow(new TrackNotFoundException("트랙을 찾을 수 없습니다."));

            assertThrows(TrackNotFoundException.class, () -> trackService.recommendTracks(userDetailsMock));
        }
    }


    @Test
    @DisplayName("모달 상제 조회 테스트")
    void getTrackDetailModal() {
        String mockTrackId = "testTrackId";
        Track mockTrack = mock(Track.class);
        Track.Artist mockArtist = mock(Track.Artist.class);

        when(mockTrack.getArtists()).thenReturn(Arrays.asList(mockArtist));
        when(mockTrack.getImage()).thenReturn("testImage");
        when(mockTrack.getAlbum()).thenReturn("testAlbum");
        when(mockArtist.getArtistName()).thenReturn("testArtistName");
        when(mockTrack.getTitle()).thenReturn("testTitle");
        when(spotifyRequestManager.getTrackInfo(mockTrackId)).thenReturn(mockTrack);

        TrackDetailModal result = trackService.getTrackDetailModal(mockTrackId);

        assertNotNull(result);
        assertEquals("testImage", result.getImage());
        assertEquals("testAlbum", result.getAlbum());
        assertEquals("testArtistName", result.getArtist());
        assertEquals("testTitle", result.getTitle());
    }

    @Test
    @DisplayName("트랙 상세 정보 조회 테스트")
    void getTrackDetail() {
        String mockTrackId = "testTrackId";
        Track mockTrack = mock(Track.class);
        Double mockAverageStar = 4.5;

        when(mockTrack.getTrackId()).thenReturn(mockTrackId);
        when(mockTrack.getTitle()).thenReturn("testTitle");
        when(mockTrack.getAlbum()).thenReturn("testAlbum");
        when(mockTrack.getImage()).thenReturn("testImage");
        when(mockTrack.getArtists()).thenReturn(Collections.emptyList());
        when(mockTrack.getArtistsStringList()).thenReturn("testArtistString");

        when(spotifyRequestManager.getTrackInfo(mockTrackId)).thenReturn(mockTrack);
        when(starRepository.findAverageStarByTrackId(mockTrackId)).thenReturn(Optional.of(mockAverageStar));

        TrackDetailDto result = trackService.getTrackDetail(mockTrackId);

        assertNotNull(result);
        assertEquals(mockTrackId, result.getTrackId());
        assertEquals("testTitle", result.getTitle());
        assertEquals("testAlbum", result.getAlbum());
        assertEquals("testImage", result.getImage());
        assertEquals(mockAverageStar, result.getAverageStar(), 0.1);
        assertEquals("testArtistString", result.getArtistsStringList());
    }


    @Test
    @DisplayName("최근 트랙 조회 테스트")
    void getRecentTracks() {
        Long mockUserId = 1L;
        User mockUser = mock(User.class);
        Recent mockRecent = mock(Recent.class);
        Track mockTrack = mock(Track.class);

        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));

        List<Recent> mockRecentList = Arrays.asList(mockRecent, mockRecent);
        when(mockRecent.getTrackId()).thenReturn("mockTrackId1", "mockTrackId2");  // 두 가지 다른 트랙 ID를 반환하도록 설정
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "creationDate"));
        when(recentRepository.findAllByUserOrderByCreationDateDesc(mockUser, pageable)).thenReturn(mockRecentList);

        List<String> mockTrackIds = Arrays.asList("mockTrackId1", "mockTrackId2");
        when(spotifyRequestManager.getTracksInfo(mockTrackIds)).thenReturn(Arrays.asList(mockTrack, mockTrack));

        List<Track> resultTracks = trackService.getRecentTracks(mockUserId);

        assertNotNull(resultTracks);
        assertEquals(2, resultTracks.size());
        verify(userRepository).findById(mockUserId);
        verify(recentRepository).findAllByUserOrderByCreationDateDesc(mockUser, pageable);
        verify(spotifyRequestManager).getTracksInfo(mockTrackIds);
    }

    @Nested
    @DisplayName("최근 트랙 생성 테스트")
    class createRecentTrack {
        @Test
        @DisplayName("총 레코드 수 20 미만일 때 트랙 생성 테스트")
        void createRecentTrack_whenUnderLimit() {
            User user = mock(User.class);
            when(recentRepository.findByUserOrderByCreationDateAsc(user)).thenReturn(new ArrayList<>());

            trackService.createRecentTrack("someTrackId", user);

            verify(recentRepository).save(any());
        }

        @Test
        @DisplayName("총 레코드 수 20 초과일 때 트랙 생성 테스트")
        void createRecentTrack_whenOverLimit() {
            User user = mock(User.class);
            Recent oldestRecent = mock(Recent.class);

            // 오래된 트랙부터 순서대로 리스트 생성
            List<Recent> recentTracks = new ArrayList<>(Arrays.asList(oldestRecent));
            recentTracks.addAll(Collections.nCopies(20, mock(Recent.class)));

            when(recentRepository.findByUserOrderByCreationDateAsc(user)).thenReturn(recentTracks);
            when(recentRepository.findByUserAndTrackId(user, "someTrackId")).thenReturn(Optional.empty());

            trackService.createRecentTrack("someTrackId", user);

            verify(recentRepository).delete(oldestRecent);
            verify(recentRepository).save(any(Recent.class));
        }
    }

    @Test
    @DisplayName("최근 트랙 7개 가져오기 테스트")
    void get7RecentTracks() {
        Recent mockRecent = mock(Recent.class);
        Track mockTrack = mock(Track.class);
        List<Recent> mockRecentList = new ArrayList<>(Collections.nCopies(7, mockRecent));
        when(recentRepository.findTop7ByOrderByCreationDateDesc(any())).thenReturn(mockRecentList);
        when(spotifyRequestManager.getTracksInfo(anyList())).thenReturn(Collections.nCopies(7, mockTrack));

        List<Top7Dto> result = trackService.get7RecentTracks();

        assertEquals(7, result.size());
    }

    @Nested
    @DisplayName("별점 설정 테스트")
    class setStarRating {
        @Test
        @DisplayName("첫 별점")
        public void setStarRating_NewStar() {
            StarDto starDto = new StarDto();
            starDto.setStar(5.0);
            String trackId = "track_1";
            ArgumentCaptor<Star> starCaptor = ArgumentCaptor.forClass(Star.class);
            when(userDetails.getUser()).thenReturn(user);
            when(starRepository.findByUserAndTrackId(user, trackId)).thenReturn(Optional.empty());

            ResponseEntity<StatusResponseDto> response = trackService.setStarRating(trackId, starDto, userDetails);

            verify(starRepository).save(starCaptor.capture());
            assertEquals(5.0, starCaptor.getValue().getStar(), 0.001);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("기존 별점")
        public void setStarRating_UpdateStar() {
            StarDto starDto = new StarDto();
            starDto.setStar(4.0);
            String trackId = "track_1";
            Star existingStar = new Star(trackId, 5.0, user);

            when(userDetails.getUser()).thenReturn(user);
            when(starRepository.findByUserAndTrackId(user, trackId)).thenReturn(Optional.of(existingStar));

            ResponseEntity<StatusResponseDto> response = trackService.setStarRating(trackId, starDto, userDetails);

            assertEquals(4.0, existingStar.getStar(), 0.001);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    @DisplayName("별점 삭제 테스트")
    void deleteStarRating() {
        String trackId = "track123";
        Star mockStar = mock(Star.class);

        when(userDetails.getUser()).thenReturn(user);
        when(starRepository.findByUserAndTrackId(user, trackId)).thenReturn(Optional.of(mockStar));

        ResponseEntity<StatusResponseDto> result = trackService.deleteStarRating(trackId, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(starRepository, times(1)).delete(mockStar);
    }
}
