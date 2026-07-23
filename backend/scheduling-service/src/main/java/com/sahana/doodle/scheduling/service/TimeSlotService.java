package com.sahana.doodle.scheduling.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sahana.doodle.scheduling.dto.CreateTimeSlotRequest;
import com.sahana.doodle.scheduling.dto.TimeSlotResponse;
import com.sahana.doodle.scheduling.dto.UpdateTimeSlotRequest;
import com.sahana.doodle.scheduling.model.TimeSlot;

public interface TimeSlotService {

	TimeSlotResponse createTimeSlot(CreateTimeSlotRequest request);

    TimeSlotResponse getTimeSlotById(Long id);

    List<TimeSlotResponse> getTimeSlotsByCalendarId(Long calendarId);

    TimeSlotResponse updateTimeSlot(
            Long id,
            UpdateTimeSlotRequest request
    );

    void deleteTimeSlot(Long id);
}
