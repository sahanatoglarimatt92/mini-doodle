package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;

public record CreateTimeSlotRequest(

        @NotNull(message = "Calendar ID is required")
        Long calendarId,

        @NotNull(message = "Start time is required")
        OffsetDateTime startTime,

        @NotNull(message = "End time is required")
        OffsetDateTime endTime

) {
}