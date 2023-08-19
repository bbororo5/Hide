package com.example.backend.util.execption;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.StatusResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	// UserNotFoundException , NotFoundTrackException , NoSuchElementException , AccessDeniedException , JsonProcessingException , IllegalArgumentException , IllegalStateException
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<StatusResponseDto> handleUserNotFoundException(UserNotFoundException e) {
		StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
		return new ResponseEntity<>(statusResponseDto, HttpStatus.NOT_FOUND);
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

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StatusResponseDto> handleIllegalStateException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		StringBuilder message = new StringBuilder();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			message.append(fieldError.getField()).append(" ").append(fieldError.getDefaultMessage()).append("; ");
		}
		StatusResponseDto statusResponseDto = new StatusResponseDto(message.toString(), false);
		return new ResponseEntity<>(statusResponseDto, HttpStatus.BAD_REQUEST);
	}
}
