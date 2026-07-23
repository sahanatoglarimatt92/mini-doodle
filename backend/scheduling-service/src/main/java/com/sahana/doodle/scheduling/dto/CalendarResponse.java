package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;

public record CalendarResponse(
        Long id,
        Long userId,
        String name,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
