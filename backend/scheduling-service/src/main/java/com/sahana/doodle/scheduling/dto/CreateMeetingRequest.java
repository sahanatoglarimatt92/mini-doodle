package com.sahana.doodle.scheduling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMeetingRequest(

        @NotNull(message = "Time slot ID is required")
        Long timeSlotId,

        @NotNull(message = "Organizer ID is required")
        Long organizerId,

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title cannot exceed 150 characters")
        String title,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description

) {
}