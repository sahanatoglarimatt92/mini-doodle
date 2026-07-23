package com.sahana.doodle.scheduling.dto;

import java.util.List;

public record UserAvailabilityResponse(
        Long userId,
        String userName,
        List<TimeRangeResponse> busySlots
) {
}