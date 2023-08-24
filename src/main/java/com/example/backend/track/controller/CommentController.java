package com.example.backend.track.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.util.StatusResponseDto;
import com.example.backend.track.dto.CommentRequestDto;
import com.example.backend.track.dto.CommentResponseDto;
import com.example.backend.track.service.CommentService;
import com.example.backend.util.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class CommentController {
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	private final CommentService commentService;

	//감상평 작성
	@PostMapping("/{track-id}/comments")
	public ResponseEntity<StatusResponseDto> createComment(@PathVariable(name = "track-id") String trackId,
		@RequestBody CommentRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("코멘트 생성");
		ResponseEntity<StatusResponseDto> response = commentService.createComment(trackId, requestDto, userDetails);
		return response;
	}

	//감상평 조회
	@GetMapping("/{track-id}/comments")
	public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable(name = "track-id") String trackId) {
		logger.info("해당 트랙의 댓글 가져오기");
		return ResponseEntity.ok(commentService.getComments(trackId));
	}

	//감상평 수정
	@PutMapping("/comments/{comment-id}")
	public ResponseEntity<StatusResponseDto> updateComment(@PathVariable(name = "comment-id") Long commentId,
		@RequestBody CommentRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("해당 코멘트 수정하기: comment ID: {}", commentId);
		return commentService.updateComment(commentId, requestDto, userDetails);
	}

	//감상평 삭제
	@DeleteMapping("/comments/{comment-id}")
	public ResponseEntity<StatusResponseDto> deleteComment(@PathVariable(name = "comment-id") Long commentId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		logger.info("해당 코멘트 삭제하기: comment ID: {}", commentId);
		return commentService.deleteComment(commentId, userDetails);
	}
}
