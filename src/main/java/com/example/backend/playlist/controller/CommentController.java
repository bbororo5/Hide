package com.example.backend.playlist.controller;

import com.example.backend.StatusResponseDto;
import com.example.backend.playlist.dto.CommentRequestDto;
import com.example.backend.playlist.dto.CommentResponseDto;
import com.example.backend.playlist.service.CommentService;
import com.example.backend.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //감상평 작성
    @PostMapping("/{track-id}/comments")
    public ResponseEntity<StatusResponseDto> createComment(@PathVariable(name = "track-id") String trackId, @RequestBody CommentRequestDto requestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.createComment(trackId, requestDto, userDetails);
    }

    //감상평 조회
    @GetMapping("/{track-id}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable(name = "track-id") String trackId) {
        return ResponseEntity.ok(commentService.getComments(trackId));
    }

    //감상평 수정
    @PutMapping("/comments/{comment-id}")
    public ResponseEntity<StatusResponseDto> updateComment(@PathVariable(name = "comment-id") Long commentId,
                                            @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(commentId, requestDto, userDetails);
    }

    //감상평 삭제
    @DeleteMapping("/comments/{comment-id}")
    public ResponseEntity<StatusResponseDto> deleteComment(@PathVariable(name = "comment-id") Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.deleteComment(commentId, userDetails);
    }
}
