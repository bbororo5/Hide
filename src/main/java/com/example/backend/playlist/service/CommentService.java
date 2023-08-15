package com.example.backend.playlist.service;

import com.example.backend.StatusResponseDto;
import com.example.backend.playlist.dto.CommentRequestDto;
import com.example.backend.playlist.dto.CommentResponseDto;
import com.example.backend.playlist.entity.Comment;
import com.example.backend.playlist.repository.CommentRepository;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public ResponseEntity<StatusResponseDto> createComment(String trackId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {


        Comment comment = new Comment(requestDto.getContent(), userDetails.getUser(), requestDto.getStar(), trackId);
        commentRepository.save(comment);

        return new ResponseEntity<>( new StatusResponseDto("감상평 작성이 완료되었습니다.",true) , HttpStatus.OK );
    }

    public List<CommentResponseDto> getComments(String trackId) {
        List<Comment> comments = commentRepository.findAllbyTrackId(trackId);
        return comments.stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    public ResponseEntity<StatusResponseDto> updateComment(Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수가 없습니다."));

        if (!comment.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new RuntimeException("게시물을 수정할 권한이 없습니다.");
        }

        comment.updateComment(requestDto);
        Comment updatedComment = commentRepository.save(comment);

        return new ResponseEntity<>( new StatusResponseDto("감상평 수정이 완료되었습니다.",true) ,HttpStatus.OK);
    }

    public ResponseEntity<StatusResponseDto> deleteComment(Long commentId, UserDetailsImpl userDetails) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수가 없습니다."));

        if (!comment.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new RuntimeException("게시물을 삭제할 권한이 없습니다.");
        }
        commentRepository.delete(comment);
        return new ResponseEntity<>( new StatusResponseDto("감상평 삭제가 완료되었습니다.",true) ,HttpStatus.OK);
    }
}
