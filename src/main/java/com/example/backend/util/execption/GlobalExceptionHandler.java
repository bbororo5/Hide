package com.example.backend.util.execption;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.StatusResponseDto;

import io.jsonwebtoken.ExpiredJwtException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // UserNotFoundException , NotFoundTrackException , NoSuchElementException , AccessDeniedException , JsonProcessingException , IllegalArgumentException , IllegalStateException
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StatusResponseDto> handleUserNotFoundException(UserNotFoundException e){
        StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(),false);
        return new ResponseEntity<>(statusResponseDto,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundTrackException.class)
    public ResponseEntity<StatusResponseDto> handleNotFoundTrackException(NotFoundTrackException e) {
        StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<StatusResponseDto> handleNoSuchElementException(NoSuchElementException e) {
        StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StatusResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<StatusResponseDto> handleJsonProcessingException(JsonProcessingException e) {
        StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StatusResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StatusResponseDto> handleIllegalStateException(IllegalStateException e) {
        StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.CONFLICT);
    }

}
