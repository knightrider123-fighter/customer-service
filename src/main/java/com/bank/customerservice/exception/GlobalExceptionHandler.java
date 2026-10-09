package com.bank.customerservice.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bank.customerservice.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 400 - Request DTO validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		Map<String, String> errors = new LinkedHashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

		return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
	}

	// 400 - Invalid or malformed JSON
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex,
			HttpServletRequest request) {

		return buildResponse(HttpStatus.BAD_REQUEST, "Request body is missing or contains invalid JSON", request,
				Collections.emptyMap());
	}

	// 400 - Other Bean Validation constraint violations
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {

		Map<String, String> errors = new LinkedHashMap<>();

		ex.getConstraintViolations()
				.forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

		return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
	}

	// 409 - Duplicate email or external ID
	@ExceptionHandler(DuplicateResourseException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateResource(DuplicateResourseException ex,
			HttpServletRequest request) {

		return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, Collections.emptyMap());
	}

	// 422 - Business rule violation
	@ExceptionHandler(BusinessRuleViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException ex,
			HttpServletRequest request) {

		return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, Collections.emptyMap());
	}

	// Common method for creating the error response
	private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message,
			HttpServletRequest request, Map<String, String> validationErrors) {

		ApiErrorResponse response = ApiErrorResponse.builder().timeStamp(LocalDateTime.now()).status(status.value())
				.error(status.getReasonPhrase()).message(message).path(request.getRequestURI())
				.validationErrors(validationErrors).build();

		return ResponseEntity.status(status).body(response);
	}

	// 500 - Unexpected server errors
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {

		ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "An unexpected error occurred",
				request.getRequestURI(), Collections.emptyMap());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
