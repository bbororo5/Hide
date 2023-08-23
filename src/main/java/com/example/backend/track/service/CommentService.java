package com.example.backend.track.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.util.StatusResponseDto;
import com.example.backend.track.dto.CommentRequestDto;
import com.example.backend.track.dto.CommentResponseDto;
import com.example.backend.track.entity.Comment;
import com.example.backend.track.repository.CommentRepository;
import com.example.backend.util.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
	private final CommentRepository commentRepository;

	public ResponseEntity<StatusResponseDto> createComment(String trackId, CommentRequestDto requestDto,
		UserDetailsImpl userDetails) {
		logger.info("댓글 작성");
		Comment comment = new Comment(requestDto.getContent(), userDetails.getUser(), trackId);
		commentRepository.save(comment);

		return new ResponseEntity<>(new StatusResponseDto("감상평 작성이 완료되었습니다.", true), HttpStatus.OK);
	}

	public List<CommentResponseDto> getComments(String trackId) {
		logger.info("댓글 조회");
		List<Comment> comments = commentRepository.findAllByTrackId(trackId);
		comments.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
		List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
		for (Comment comment : comments) {
			commentResponseDtoList.add(new CommentResponseDto(comment));
		}
		return commentResponseDtoList;
	}

	@Transactional
	public ResponseEntity<StatusResponseDto> updateComment(Long commentId, CommentRequestDto requestDto,
		UserDetailsImpl userDetails) {
		logger.info("댓글 수정");
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NoSuchElementException("게시물을 찾을 수가 없습니다."));

		if (!comment.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
			throw new AccessDeniedException("게시물을 수정할 권한이 없습니다.");
		}
		comment.updateComment(requestDto);
		commentRepository.save(comment);
		return new ResponseEntity<>(new StatusResponseDto("감상평 수정이 완료되었습니다.", true), HttpStatus.OK);
	}

	public ResponseEntity<StatusResponseDto> deleteComment(Long commentId, UserDetailsImpl userDetails) {
		logger.info("댓글 삭제");
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NoSuchElementException("게시물을 찾을 수가 없습니다."));

		if (!comment.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
			throw new AccessDeniedException("게시물을 삭제할 권한이 없습니다.");
		}
		commentRepository.delete(comment);
		return new ResponseEntity<>(new StatusResponseDto("감상평 삭제가 완료되었습니다.", true), HttpStatus.OK);
	}
}
