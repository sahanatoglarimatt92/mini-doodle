package com.sahana.doodle.scheduling.mapper;

import org.springframework.stereotype.Component;

import com.sahana.doodle.scheduling.dto.CalendarResponse;
import com.sahana.doodle.scheduling.model.Calendar;

@Component
public class CalendarMapper {

    public CalendarResponse toResponse(Calendar calendar) {
        return new CalendarResponse(
                calendar.getId(),
                calendar.getUser().getId(),
                calendar.getName(),
                calendar.getCreatedAt(),
                calendar.getUpdatedAt()
        );
    }
}