package com.example.backend.util.execption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.StatusResponseDto;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<StatusResponseDto> handleExpiredJwtException(ExpiredJwtException e) {
		return new ResponseEntity<>(new StatusResponseDto("Expired JWT token, 만료된 JWT token 입니다." , false),HttpStatus.UNAUTHORIZED);
	}

}
