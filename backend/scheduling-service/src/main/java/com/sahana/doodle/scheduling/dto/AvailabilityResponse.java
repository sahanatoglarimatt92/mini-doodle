package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record AvailabilityResponse(
        OffsetDateTime searchStartTime,
        OffsetDateTime searchEndTime,
        List<UserAvailabilityResponse> users,
        List<TimeRangeResponse> commonFreeSlots
) {
}
