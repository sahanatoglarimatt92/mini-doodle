package com.sahana.doodle.scheduling.exception;

import java.time.OffsetDateTime;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleUserNotFound(
			UserNotFoundException exception,
			HttpServletRequest request
			) {
		return buildErrorResponse(
				HttpStatus.NOT_FOUND,
				exception.getMessage(),
				request.getRequestURI(),
				null
				);
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(
			DuplicateEmailException exception,
			HttpServletRequest request
			) {
		return buildErrorResponse(
				HttpStatus.CONFLICT,
				exception.getMessage(),
				request.getRequestURI(),
				null
				);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationErrors(
			MethodArgumentNotValidException exception,
			HttpServletRequest request
			) {
		Map<String, String> validationErrors = new LinkedHashMap<>();

		exception.getBindingResult()
		.getFieldErrors()
		.forEach(error ->
		validationErrors.put(
				error.getField(),
				error.getDefaultMessage()
				)
				);

		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				"Request validation failed",
				request.getRequestURI(),
				validationErrors
				);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
			Exception exception,
			HttpServletRequest request) {

		exception.printStackTrace();

		ApiErrorResponse response = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(response);
	}

	private ResponseEntity<ApiErrorResponse> buildErrorResponse(
			HttpStatus status,
			String message,
			String path,
			Map<String, String> validationErrors
			) {
		ApiErrorResponse response = new ApiErrorResponse(
				OffsetDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				path,
				validationErrors
				);

		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler(CalendarNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleCalendarNotFound(
			CalendarNotFoundException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(error);
	}

	@ExceptionHandler(CalendarAlreadyExistsException.class)
	public ResponseEntity<ApiErrorResponse> handleCalendarAlreadyExists(
			CalendarAlreadyExistsException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.CONFLICT.value(),
				"Conflict",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(error);
	}

	@ExceptionHandler({
		InvalidTimeRangeException.class,
		OverlappingTimeSlotException.class,
		IllegalStateException.class
	})
	public ResponseEntity<ApiErrorResponse> handleBadRequest(
			RuntimeException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(error);
	}

	@ExceptionHandler(TimeSlotNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleTimeSlotNotFound(
			TimeSlotNotFoundException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(error);
	}

	@ExceptionHandler(MeetingNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleMeetingNotFound(
			MeetingNotFoundException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(error);
	}

	@ExceptionHandler(TimeSlotAlreadyBookedException.class)
	public ResponseEntity<ApiErrorResponse> handleTimeSlotAlreadyBooked(
			TimeSlotAlreadyBookedException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.CONFLICT.value(),
				"Conflict",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(error);
	}

	@ExceptionHandler({
		ParticipantAlreadyAddedException.class,
		ParticipantNotFoundInMeetingException.class
	})
	public ResponseEntity<ApiErrorResponse> handleParticipantConflict(
			RuntimeException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.CONFLICT.value(),
				"Conflict",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(error);
	}

	@ExceptionHandler(ParticipantUnavailableException.class)
	public ResponseEntity<ApiErrorResponse> handleParticipantUnavailable(
			ParticipantUnavailableException exception,
			HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.CONFLICT.value(),
				"Conflict",
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(error);
	}

	@ExceptionHandler(InvalidAvailabilityRequestException.class)
	public ResponseEntity<ApiErrorResponse>
	handleInvalidAvailabilityRequest(
			InvalidAvailabilityRequestException exception,
			HttpServletRequest request) {

		ApiErrorResponse response = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(response);
	}

	@ExceptionHandler({
		TimeSlotUnavailableException.class,
		InvalidMeetingTimeSlotException.class
	})
	public ResponseEntity<ApiErrorResponse> handleMeetingConflict(
			RuntimeException exception,
			HttpServletRequest request) {

		ApiErrorResponse response = new ApiErrorResponse(
				OffsetDateTime.now(),
				HttpStatus.CONFLICT.value(),
				HttpStatus.CONFLICT.getReasonPhrase(),
				exception.getMessage(),
				request.getRequestURI(),
				null
				);

		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(response);
	}
}
