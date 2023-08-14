package com.example.backend.playlist.controller;

import com.example.backend.playlist.dto.CommentRequestDto;
import com.example.backend.playlist.dto.CommentResponseDto;
import com.example.backend.playlist.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/musics/{user-id}")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //감상평 작성
    @PostMapping("/comments")
    public CommentResponseDto createComment(@PathVariable("userId") Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest req) {
        return commentService.createComment(id, requestDto, req);
    }

    //감상평 조회
    @GetMapping("/comments")
    public List<CommentResponseDto> getComments(@PathVariable("userId") Long id) {
        return commentService.getComments(id);
    }

    //감상평 수정
    @PutMapping("/comments")
    public CommentResponseDto updateComment(@PathVariable("userId") Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest req) {
        return commentService.updateComment(id, requestDto, req);
    }

    //감상평 삭제
    @DeleteMapping("/comments")
    public ResponseEntity<String> deleteComment(@PathVariable("userId") Long id, HttpServletRequest req) {
        commentService.deleteComment(id, req);
        return ResponseEntity.ok().body("게시글을 성공적으로 삭제하였습니다.");
    }
}
