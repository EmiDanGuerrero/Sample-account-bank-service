package com.bank_services.account.infrastructure.error;

import com.bank_services.account.domain.exception.DuplicateResourceException;
import com.bank_services.account.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@SuppressWarnings("unused")
	private void dummyMethod(String param) {
		// no-op
	}

	private HttpServletRequest mockRequest(String uri) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(uri);
		return request;
	}

	private WebRequest mockWebRequest(String uri) {
		WebRequest webRequest = mock(WebRequest.class);
		when(webRequest.getDescription(false)).thenReturn("uri=" + uri);
		return webRequest;
	}

	// --- tests específicos ---------------------------------------------------

	@Test
	void handleResourceNotFound_shouldReturn404() {
		ResourceNotFoundException ex = new ResourceNotFoundException("not found");
		HttpServletRequest request = mockRequest("/api/test");

		ResponseEntity<ApiError> response = handler.handleResourceNotFound(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(404);
		assertThat(response.getBody().getPath()).isEqualTo("/api/test");
		assertThat(response.getBody().getMessage()).contains("not found");
	}

	@Test
	void handleDuplicateResource_shouldReturn409() {
		DuplicateResourceException ex = new DuplicateResourceException("duplicate");
		HttpServletRequest request = mockRequest("/api/test");

		ResponseEntity<ApiError> response = handler.handleDuplicateResource(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(409);
		assertThat(response.getBody().getMessage()).contains("duplicate");
	}

	@Test
	void handleConstraintViolation_shouldReturn400() {
		ConstraintViolationException ex = new ConstraintViolationException("constraint failed", Collections.emptySet());
		HttpServletRequest request = mockRequest("/api/test");

		ResponseEntity<ApiError> response = handler.handleConstraintViolation(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(400);
		assertThat(response.getBody().getMessage()).contains("constraint failed");
	}

	@Test
	void handleGenericException_shouldReturn500() {
		Exception ex = new RuntimeException("boom");
		HttpServletRequest request = mockRequest("/api/test");

		ResponseEntity<ApiError> response = handler.handleGenericException(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(500);
		assertThat(response.getBody().getMessage()).contains("boom");
	}

	@Test
	void handleRestClientResponse_shouldMapStatusFromException() {
		RestClientResponseException ex = new RestClientResponseException("Upstream error", 400, "Bad Request",
				new HttpHeaders(), "bad".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		HttpServletRequest request = mockRequest("/api/test");

		ResponseEntity<ApiError> response = handler.handleRestClientResponse(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(400);
		assertThat(response.getBody().getMessage()).contains("Upstream error");
	}

	@Test
	void handleRestClientResponse_shouldFallbackTo500_whenStatusUnknown() {
		RestClientResponseException ex = new RestClientResponseException("Unknown status", 999, "???",
				new HttpHeaders(), null, null);
		HttpServletRequest request = mockRequest("/api/test");

		ResponseEntity<ApiError> response = handler.handleRestClientResponse(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(500);
	}

	@Test
	void handleMethodArgumentNotValid_shouldReturn400WithFormattedMessage() throws Exception {
		// Preparamos BindingResult con un FieldError
		Object target = new Object();
		BindingResult bindingResult = new BeanPropertyBindingResult(target, "bankAccountRequest");
		bindingResult.addError(
				new FieldError("bankAccountRequest", "ownerName", null, false, null, null, "must not be blank"));

		// Construimos MethodParameter usando el método dummy
		Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class);
		MethodParameter parameter = new MethodParameter(method, 0);

		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

		WebRequest webRequest = mockWebRequest("/api/accounts");

		ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(ex, HttpHeaders.EMPTY,
				HttpStatus.BAD_REQUEST, webRequest);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isInstanceOf(ApiError.class);
		ApiError body = (ApiError) response.getBody();
		assertThat(body.getStatus()).isEqualTo(400);
		assertThat(body.getPath()).isEqualTo("/api/accounts");
		assertThat(body.getMessage()).contains("ownerName").contains("must not be blank");
	}

	@Test
	void handleHttpMessageNotReadable_shouldReturn400WithMostSpecificCause() {
		RuntimeException cause = new RuntimeException("Expected value at line 1");
		HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON", cause, null);

		WebRequest webRequest = mockWebRequest("/api/accounts");

		ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(ex, HttpHeaders.EMPTY,
				HttpStatus.BAD_REQUEST, webRequest);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isInstanceOf(ApiError.class);
		ApiError body = (ApiError) response.getBody();
		assertThat(body.getStatus()).isEqualTo(400);
		assertThat(body.getPath()).isEqualTo("/api/accounts");
		assertThat(body.getMessage()).contains("Expected value at line 1");
	}
}
