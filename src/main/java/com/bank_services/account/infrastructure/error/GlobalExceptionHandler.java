package com.bank_services.account.infrastructure.error;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bank_services.account.domain.exception.DuplicateResourceException;
import com.bank_services.account.domain.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// 404 - cuando el servicio de dominio no encuentra el recurso
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	// 409 - duplicados al crear/actualizar
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiError> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	// 4xx cuando nuestro RestClient recibe errores de otros endpoints y sube la
	// excepción
	@ExceptionHandler(RestClientResponseException.class)
	public ResponseEntity<ApiError> handleRestClientResponse(RestClientResponseException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.getRawStatusCode());
		if (status == null) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		ApiError error = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(status).body(error);
	}

	// 400 - errores de validación @Valid en @RequestBody
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String path = request.getDescription(false).replace("uri=", "");

		String validationErrors = ex.getBindingResult().getFieldErrors().stream().map(this::formatFieldError)
				.collect(Collectors.joining("; "));

		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed",
				validationErrors, path);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	private String formatFieldError(FieldError fieldError) {
		return "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage());
	}

	// 400 - JSON mal formado, tipos incorrectos, etc.
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String path = request.getDescription(false).replace("uri=", "");
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Malformed JSON request",
				ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage(), path);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 400 - validaciones tipo @NotNull en parámetros, etc.
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Constraint Violation",
				ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 500 - catch-all para cualquier otra excepción no manejada
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error", ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
