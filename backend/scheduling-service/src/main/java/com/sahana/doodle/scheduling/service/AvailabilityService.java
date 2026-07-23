package com.sahana.doodle.scheduling.service;

import java.time.OffsetDateTime;
import java.util.List;

import com.sahana.doodle.scheduling.dto.AvailabilityResponse;

public interface AvailabilityService {

	AvailabilityResponse getAvailability(
			List<Long> userIds,
			OffsetDateTime startTime,
			OffsetDateTime endTime
			);
}