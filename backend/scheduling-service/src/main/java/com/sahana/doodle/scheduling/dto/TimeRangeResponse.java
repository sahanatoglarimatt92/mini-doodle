package com.sahana.doodle.scheduling.dto;

import java.time.OffsetDateTime;

public record TimeRangeResponse(
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {
}
