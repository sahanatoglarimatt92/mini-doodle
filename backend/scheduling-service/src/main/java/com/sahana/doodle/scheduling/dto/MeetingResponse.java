package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record MeetingResponse(
        Long id,
        Long timeSlotId,
        Long organizerId,
        String organizerName,
        String title,
        String description,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        List<ParticipantResponse> participants,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
