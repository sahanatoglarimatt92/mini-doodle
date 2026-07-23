package com.sahana.doodle.scheduling.dto;

import jakarta.validation.constraints.NotNull;

public record RescheduleMeetingRequest(

		@NotNull Long newTimeSlotId
		
) {
}
