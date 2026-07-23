package com.sahana.doodle.scheduling.mapper;

import org.springframework.stereotype.Component;

import com.sahana.doodle.scheduling.dto.TimeSlotResponse;
import com.sahana.doodle.scheduling.model.TimeSlot;

@Component
public class TimeSlotMapper {

    public TimeSlotResponse toResponse(TimeSlot timeSlot) {
        return new TimeSlotResponse(
                timeSlot.getId(),
                timeSlot.getCalendar().getId(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                timeSlot.getStatus(),
                timeSlot.getCreatedAt(),
                timeSlot.getUpdatedAt()
        );
    }
}