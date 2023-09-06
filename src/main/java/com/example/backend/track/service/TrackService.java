package com.example.backend.track.service;

import com.example.backend.playlist.repository.PlayListRepository;
import com.example.backend.track.dto.*;
import com.example.backend.track.entity.QStar;
import com.example.backend.track.entity.Recent;
import com.example.backend.track.entity.Star;
import com.example.backend.track.entity.TrackCount;
import com.example.backend.track.repository.RecentRepository;
import com.example.backend.track.repository.StarRepository;
import com.example.backend.track.repository.TrackCountRepository;
import com.example.backend.track.repository.TrackCountRepositoryImpl;
import com.example.backend.user.entity.QImage;
import com.example.backend.user.entity.QUser;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.globalDto.StatusResponseDto;
import com.example.backend.util.execption.DataNotFoundException;
import com.example.backend.util.execption.TrackNotFoundException;
import com.example.backend.util.execption.UserNotFoundException;
import com.example.backend.util.security.UserDetailsImpl;
import com.example.backend.util.spotify.SpotifyRequestManager;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackService {
	private static final Logger logger = LoggerFactory.getLogger(TrackService.class);
	private final TrackCountRepository trackCountRepository;
	private final TrackCountRepositoryImpl trackCountRepositoryImpl;
	private final UserRepository userRepository;
	private final SpotifyRequestManager spotifyRequestManager;
	private final RecentRepository recentRepository;
	private final PlayListRepository playListRepository;
	private final StarRepository starRepository;
	private final JPAQueryFactory jpaQueryFactory;

	public void increasePlayCount(String trackId) {
		logger.info("트랙의 플레이 카운트 1 증가");
		TrackCount trackCount = trackCountRepository.findByTrackId(trackId)
			.orElse(new TrackCount(trackId, 0)); // 트랙이 없는 경우 새 TrackCount 생성
		handleTrackCountLimit();
		trackCount.increasePlayCount();
		trackCountRepository.save(trackCount);
	}

	private void handleTrackCountLimit() {
		logger.info("트랙 카운트 레코드가 500개를 넘는지 검사");
		long count = trackCountRepository.count();
		if (count >= 500) {
			removeOldestTrackCount();
		}
	}

	private void removeOldestTrackCount() {
		logger.info("500이 넘어 가장 오래된 트랙 카운트 제거");
		trackCountRepository.findFirstByOrderByCreatedAtAsc().ifPresent(trackCountRepository::delete);
	}

	@Transactional(readOnly = true)
	public List<Track> getTopTracksByAllUser() {
		logger.info("종합 플레이 카운트로 탑10 트랙 가져오기");
		Pageable top10 = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "playCount"));
		List<TrackCount> trackCounts = trackCountRepository.findAll(top10).getContent();

		List<String> trackIds = trackCounts.stream()
			.map(TrackCount::getTrackId)
			.collect(Collectors.toList());

		return spotifyRequestManager.getTracksInfo(trackIds);
	}


	@Transactional(readOnly = true)
	public List<Track> recommendTracks(UserDetailsImpl userDetails) {
		logger.info("추천 트랙 받아오기");
		User user = userDetails.getUser();
		Set<String> trackIds = new HashSet<>();
		trackIds.addAll(trackCountRepositoryImpl.findTrackIdsFromFollowing(user));
		trackIds.addAll(trackCountRepositoryImpl.findTrackIdsFromFollower(user));
		trackIds.addAll(trackCountRepositoryImpl.findHighRatedAndRelatedTracks(user));
		trackIds.addAll(trackCountRepositoryImpl.findRecent5TracksFromUser(user));
		List<String> trackIdsList = new ArrayList<>(trackIds);
		if(trackIdsList.size()<2){
			trackIdsList.add("7iN1s7xHE4ifF5povM6A48");
			trackIdsList.add("5aHwYjiSGgJAxy10mBMlDT");
		}
		Collections.shuffle(trackIdsList);
		List<Track> recommendedTracks;
		try {
			recommendedTracks = spotifyRequestManager.getTracksInfo(trackIdsList);
		} catch (TrackNotFoundException e) {
			throw new TrackNotFoundException("트랙을 찾을 수 없습니다.");
		}
		return recommendedTracks.stream().distinct().collect(Collectors.toList());
	}

	public TrackDetailModal getTrackDetailModal(String trackId) {
		logger.info("모달용 트랙 세부사항 조회");
		Track track = spotifyRequestManager.getTrackInfo(trackId);
		String artistName = track.getArtists().get(0).getArtistName();
		String trackTitle = track.getTitle();


		return TrackDetailModal.builder()
			.image(track.getImage())
			.album(track.getAlbum())
			.artist(artistName)
			.title(trackTitle)
			.build();
	}

	@Transactional(readOnly = true)
	public TrackDetailDto getTrackDetail(String trackId) {
		logger.info("트랙 세부사항 조회");
		Track track = spotifyRequestManager.getTrackInfo(trackId);
		Double averageStar = starRepository.findAverageStarByTrackId(trackId).orElse(null);
		TrackDetailDto trackDetailDto = new TrackDetailDto(track, averageStar);
		return trackDetailDto;
	}

	@Transactional(readOnly = true)
	public List<Track> getRecentTracks(Long userId) {
		logger.info("해당 유저의 최근 들은 트랙 조회");
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));
		Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "creationDate"));
		List<Recent> recentList = recentRepository.findAllByUserOrderByCreationDateDesc(user,pageable);
		List<String> trackIds = recentList.stream()
			.map(Recent::getTrackId)
			.toList();
		return spotifyRequestManager.getTracksInfo(trackIds);
	}

	@Transactional
	public void createRecentTrack(String trackId, User user) {
		logger.info("최근 들은 트랙에 추가");
		List<Recent> recentTracks = recentRepository.findByUserOrderByCreationDateAsc(user);
		if (recentTracks.size() >= 20) {
			// 가장 오래된 곡 제거
			recentRepository.delete(recentTracks.get(0));
		}
		Recent newRecent = new Recent(trackId, user);
		Recent recent = recentRepository.findByUserAndTrackId(user,trackId).orElse(null);
		if(recent==null){
			recentRepository.save(newRecent);
		}else{
			recentRepository.delete(recent);
			recentRepository.save(newRecent);
		}
	}

	@Transactional(readOnly = true)
	public List<Top7Dto> get7RecentTracks() {
		logger.info("최근 들은 트랙 top7 조회");
		Pageable topSeven = PageRequest.of(0, 7);
		List<Recent> recent7tracts = recentRepository.findTop7ByOrderByCreationDateDesc(topSeven);
		List<String> trackIds = recent7tracts.stream()
			.map(Recent::getTrackId)
			.toList();
		List<Track> top7TrackList = spotifyRequestManager.getTracksInfo(trackIds);
		return top7TrackList.stream().map(Top7Dto::new).toList();
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> setStarRating(String trackId, StarDto starDto,
		UserDetailsImpl userDetails) {
		logger.info("평점 등록");
		Star star = starRepository.findByUserAndTrackId(userDetails.getUser(), trackId)
			.orElse(null);
		if (star != null) {
			star.updateStar(starDto.getStar());
			starRepository.save(star);
		} else {
			Star newStar = new Star(trackId, starDto.getStar(), userDetails.getUser());
			starRepository.save(newStar);
		}
		return new ResponseEntity<>(new StatusResponseDto("별점 등록 완료.", true), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> deleteStarRating(String trackId, UserDetailsImpl userDetails) {
		logger.info("평점 제거");
		Star star = starRepository.findByUserAndTrackId(userDetails.getUser(), trackId)
			.orElse(null);
		if (star != null) {
			starRepository.delete(star);
			return new ResponseEntity<>(new StatusResponseDto("별점 삭제 완료.", true), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new StatusResponseDto("별점이 없어서 삭제할 수 없습니다.", false), HttpStatus.BAD_REQUEST);
		}
	}

	@Transactional(readOnly = true)
	public ResponseEntity<List<StarListResponseDto>> getStarList(String trackId) {
		logger.info("평점 리스트 조회");
		QStar qStar = QStar.star1;
		QUser qUser = QUser.user;
		QImage qImage = QImage.image;

		List<StarListResponseDto> result = jpaQueryFactory
				.select(Projections.constructor(StarListResponseDto.class,
						qUser.userId,
						qUser.nickname,
						qImage.imageUrl,
						qStar.star
				))
				.from(qStar)
				.leftJoin(qStar.user, qUser)
				.leftJoin(qUser.image, qImage)
				.where(qStar.trackId.eq(trackId))
				.fetch();

		if (result == null || result.isEmpty()) {
			logger.warn("잘못된 트랙 아이디로 평점 리스트 조회");
			throw new DataNotFoundException("데이터가 비어있습니다. 트랙아이디: " + trackId);
		}

		return new ResponseEntity<>(result,HttpStatus.OK);
	}
}