package com.sahana.doodle.scheduling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record  CreateCalendarRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Calendar name is required")
        String name

) {
}