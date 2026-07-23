package com.sahana.doodle.scheduling.dto;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddParticipantsRequest(

		@NotEmpty(message = "At least one participant is required")
		Set<
		@NotNull(message = "Participant ID cannot be null")
		Long
		> participantIds

		) {
}
