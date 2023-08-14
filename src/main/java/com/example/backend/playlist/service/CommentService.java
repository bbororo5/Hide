package com.example.backend.playlist.service;

import com.example.backend.playlist.dto.CommentRequestDto;
import com.example.backend.playlist.dto.CommentResponseDto;
import com.example.backend.playlist.entity.Comment;
import com.example.backend.playlist.repository.CommentRepository;
import com.example.backend.playlist.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public CommentResponseDto createComment(Long id, CommentRequestDto requestDto, HttpServletRequest req) {


        Comment comment = new Comment(requestDto.getNickname(), requestDto.getContent(), requestDto.getStar());
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> getComments(Long id) {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest req) {
        String token = auth(req);
        String userId = getId(token);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수가 없습니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("게시물을 수정할 권한이 없습니다.");
        }

        comment.setContent(requestDto.getContent());
        Comment updatedComment = commentRepository.save(comment);

        return new CommentResponseDto(updatedComment);
    }

    public void deleteComment(Long id, HttpServletRequest req) {
        String token = auth(req);
        String userId = getId(token);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수가 없습니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("게시물을 삭제할 권한이 없습니다.");
        }

        commentRepository.deleteById(id);
    }

    private String auth(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("인증되지 않았습니다.");
        }
        String subToken = token.substring(7);
        if (!jwtUtil.validateJwt(subToken)) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }
        return subToken;
    }

    private String getId(String token) {
        return jwtUtil.extractUserId(token);
    }
}
