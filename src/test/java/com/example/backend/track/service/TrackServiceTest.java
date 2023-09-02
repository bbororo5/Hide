package com.example.backend.track.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.TrackCountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

	@InjectMocks
	private TrackService trackService;

	@Mock
	private TrackCountRepository trackCountRepository;

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
	void getTopTracksByAllUser() {
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