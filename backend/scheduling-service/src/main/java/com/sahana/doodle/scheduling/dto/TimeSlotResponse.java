package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;

import com.sahana.doodle.scheduling.model.SlotStatus;

public record TimeSlotResponse(
        Long id,
        Long calendarId,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        SlotStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}