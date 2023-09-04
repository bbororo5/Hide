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
import org.springframework.web.client.RestClientException;

import com.example.backend.util.globalDto.StatusResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<StatusResponseDto> handleUserNotFoundException(UserNotFoundException e) {
		StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
		return new ResponseEntity<>(statusResponseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(TokenNotFoundException.class)
	public ResponseEntity<StatusResponseDto> handleNotFoundTokenException(TrackNotFoundException e) {
		StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
		return new ResponseEntity<>(statusResponseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(TrackNotFoundException.class)
	public ResponseEntity<StatusResponseDto> handleNotFoundTrackException(TrackNotFoundException e) {
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
		log.error(e.getMessage(),e);
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
		log.error(e.getMessage(),e);
		StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
		return new ResponseEntity<>(statusResponseDto, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StatusResponseDto> handleIllegalStateException(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException",e);
		BindingResult bindingResult = e.getBindingResult();
		StringBuilder message = new StringBuilder();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			message.append(fieldError.getField()).append(" ").append(fieldError.getDefaultMessage()).append("; ");
		}
		StatusResponseDto statusResponseDto = new StatusResponseDto(message.toString(), false);
		return new ResponseEntity<>(statusResponseDto, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(RestClientException.class)
	public ResponseEntity<?> handleRestClientException(RestClientException e) {
		log.error("REST 클라이언트 호출 중 문제가 발생했습니다.", e);
		StatusResponseDto response = new StatusResponseDto("서버와 통신 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.", false);
		return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<StatusResponseDto> handleDataNotFoundException(DataNotFoundException e) {
		log.error(e.getMessage(),e);
		StatusResponseDto statusResponseDto = new StatusResponseDto(e.getMessage(), false);
		return new ResponseEntity<>(statusResponseDto, HttpStatus.CONFLICT);
	}
}
