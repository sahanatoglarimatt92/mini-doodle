package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String timezone,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}