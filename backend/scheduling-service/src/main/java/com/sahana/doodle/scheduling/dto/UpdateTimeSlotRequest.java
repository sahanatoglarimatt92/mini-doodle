package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;

public record UpdateTimeSlotRequest(

        @NotNull(message = "Start time is required")
        OffsetDateTime startTime,

        @NotNull(message = "End time is required")
        OffsetDateTime endTime

) {
}