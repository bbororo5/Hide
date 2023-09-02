package com.example.backend.util.spotify;

import com.example.backend.track.dto.Track;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpotifyRequestManagerTest {

    @InjectMocks
    private SpotifyRequestManager spotifyRequestManager;

    @Mock
    private TrackSpotifyRequest trackSpotifyRequest;

    @Test
    @DisplayName("트랙 정보 가져오기 테스트")
    public void testGetTracksInfo() {
        // Mock 데이터 설정
        List<String> mockTrackIds = Arrays.asList("1", "2", "3");

        Track.Artist artist1 = Track.Artist.builder()
                .artistName("artistName1")
                .build();
        Track.Artist artist2 = Track.Artist.builder()
                .artistName("artistName2")
                .build();
        List<Track.Artist> artistList = Arrays.asList(artist1, artist2);

        Track.Genre genre1 = Track.Genre.builder()
                .genre("genreName1")
                .build();
        Track.Genre genre2 = Track.Genre.builder()
                .genre("genreName2")
                .build();
        List<Track.Genre> genreList = Arrays.asList(genre1, genre2);

        Track track1 = Track.builder()
                .id("1")
                .title("Title1")
                .album("Album1")
                .image("Image1")
                .artists(artistList)
                .genre(genreList)
                .build();

        Track track2 = Track.builder()
                .id("2")
                .title("Title2")
                .album("Album2")
                .image("Image2")
                .artists(artistList)
                .genre(genreList)
                .build();

        Track track3 = Track.builder()
                .id("3")
                .title("Title3")
                .album("Album3")
                .image("Image3")
                .artists(artistList)
                .genre(genreList)
                .build();

        List<Track> mockTracks = Arrays.asList(track1, track2, track3);

        // Mockito 설정
        when(trackSpotifyRequest.fetchDataTrackList(anyString())).thenReturn(mockTracks);

        // 메소드 실행 및 검증
        List<Track> result = spotifyRequestManager.getTracksInfo(mockTrackIds);

        assertNotNull(result);
        assertEquals(3, result.size());
    }
}