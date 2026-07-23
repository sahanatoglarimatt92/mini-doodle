package com.sahana.doodle.scheduling.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sahana.doodle.scheduling.dto.AvailabilityResponse;
import com.sahana.doodle.scheduling.service.AvailabilityService;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

	private final AvailabilityService availabilityService;

	public AvailabilityController(
			AvailabilityService availabilityService) {
		this.availabilityService = availabilityService;
	}

	@GetMapping
	public ResponseEntity<AvailabilityResponse> getAvailability(
			@RequestParam List<Long> userIds,

			@RequestParam
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
			OffsetDateTime startTime,

			@RequestParam
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
			OffsetDateTime endTime) {

		AvailabilityResponse response =
				availabilityService.getAvailability(
						userIds,
						startTime,
						endTime
						);

		return ResponseEntity.ok(response);
	}
}
