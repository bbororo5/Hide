package com.example.backend.track.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.playlist.entity.QPlaylist;
import com.example.backend.track.entity.QRecent;
import com.example.backend.track.entity.QStar;
import com.example.backend.user.entity.QFollow;
import com.example.backend.user.entity.QUser;
import com.example.backend.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class TrackCountRepositoryImpl implements TrackCountRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	@Autowired
	public TrackCountRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	QUser qUser = QUser.user;
	QFollow qFollow = QFollow.follow;
	QPlaylist qPlaylist = QPlaylist.playlist;
	QStar qStar = QStar.star1;
	QRecent qRecent = QRecent.recent;

	@Override
	public List<String> findTrackIdsFromFollowing(User currentUser) {
		return jpaQueryFactory.select(qPlaylist.trackId)
			.from(qFollow)
			.leftJoin(qFollow.toUser, qUser)
			.leftJoin(qUser.playlists, qPlaylist)
			.where(qFollow.fromUser.eq(currentUser),qPlaylist.trackId.isNotNull())
			.orderBy(qPlaylist.id.desc())
			.limit(10)
			.fetch();
	}

	@Override
	public List<String> findTrackIdsFromFollower(User currentUser) {
		return jpaQueryFactory.select(qPlaylist.trackId)
			.from(qFollow)
			.leftJoin(qFollow.fromUser, qUser)
			.leftJoin(qUser.playlists, qPlaylist)
			.where(qFollow.toUser.eq(currentUser),qPlaylist.trackId.isNotNull())
			.orderBy(qPlaylist.id.desc())
			.limit(10)
			.fetch();
	}

	@Override
	public List<String> findHighRatedAndRelatedTracks(User currentUser) {
		List<String> highRatedTrackIds = jpaQueryFactory.select(qStar.trackId)
			.from(qStar)
			.where(qStar.user.eq(currentUser), qStar.star.goe(4.0))
			.fetch();
		return jpaQueryFactory.select(qPlaylist.trackId)
			.from(qStar)
			.leftJoin(qStar.user, qUser)
			.leftJoin(qUser.playlists, qPlaylist)
			.where(qStar.trackId.in(highRatedTrackIds), qStar.star.goe(4.0), qPlaylist.trackId.isNotNull())
			.orderBy(qPlaylist.id.desc())
			.limit(10)
			.fetch();
	}

	@Override
	public List<String> findRecent5TracksFromUser(User currentUser) {
		return jpaQueryFactory.select(qRecent.trackId)
			.from(qRecent)
			.where(qRecent.user.eq(currentUser))
			.orderBy(qRecent.creationDate.desc())
			.limit(5)
			.fetch();
	}
}
