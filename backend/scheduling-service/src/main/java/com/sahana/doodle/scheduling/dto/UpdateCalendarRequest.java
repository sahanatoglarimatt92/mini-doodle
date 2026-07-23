package com.sahana.doodle.scheduling.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCalendarRequest(

        @NotBlank(message = "Calendar name is required")
        String name

) {
}