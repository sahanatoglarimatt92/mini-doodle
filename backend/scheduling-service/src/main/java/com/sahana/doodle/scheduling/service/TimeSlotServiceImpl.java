package com.sahana.doodle.scheduling.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahana.doodle.scheduling.exception.CalendarNotFoundException;
import com.sahana.doodle.scheduling.model.Calendar;
import com.sahana.doodle.scheduling.repository.CalendarRepository;
import com.sahana.doodle.scheduling.dto.CreateTimeSlotRequest;
import com.sahana.doodle.scheduling.dto.TimeSlotResponse;
import com.sahana.doodle.scheduling.dto.UpdateTimeSlotRequest;
import com.sahana.doodle.scheduling.exception.InvalidTimeRangeException;
import com.sahana.doodle.scheduling.exception.OverlappingTimeSlotException;
import com.sahana.doodle.scheduling.exception.TimeSlotNotFoundException;
import com.sahana.doodle.scheduling.mapper.TimeSlotMapper;
import com.sahana.doodle.scheduling.model.SlotStatus;
import com.sahana.doodle.scheduling.model.TimeSlot;
import com.sahana.doodle.scheduling.repository.TimeSlotRepository;

@Service
@Transactional
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final CalendarRepository calendarRepository;
    private final TimeSlotMapper timeSlotMapper;

    public TimeSlotServiceImpl(
            TimeSlotRepository timeSlotRepository,
            CalendarRepository calendarRepository,
            TimeSlotMapper timeSlotMapper) {
        this.timeSlotRepository = timeSlotRepository;
        this.calendarRepository = calendarRepository;
        this.timeSlotMapper = timeSlotMapper;
    }

    @Override
    public TimeSlotResponse createTimeSlot(CreateTimeSlotRequest request) {

        validateTimeRange(request.startTime(), request.endTime());

        Calendar calendar = calendarRepository.findById(request.calendarId())
                .orElseThrow(() ->
                        new CalendarNotFoundException(
                                "Calendar not found with id: "
                                        + request.calendarId()
                        )
                );

        boolean overlaps = timeSlotRepository.existsOverlappingSlot(
                calendar.getId(),
                request.startTime(),
                request.endTime()
        );

        if (overlaps) {
            throw new OverlappingTimeSlotException(
                    "The requested time slot overlaps an existing slot"
            );
        }

        OffsetDateTime now = OffsetDateTime.now();

        TimeSlot timeSlot = new TimeSlot(
                calendar,
                request.startTime(),
                request.endTime(),
                SlotStatus.FREE,
                now,
                now
        );

        TimeSlot savedTimeSlot =
                timeSlotRepository.save(timeSlot);

        return timeSlotMapper.toResponse(savedTimeSlot);
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSlotResponse getTimeSlotById(Long id) {
        return timeSlotMapper.toResponse(findTimeSlotById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getTimeSlotsByCalendarId(
            Long calendarId) {

        if (!calendarRepository.existsById(calendarId)) {
            throw new CalendarNotFoundException(
                    "Calendar not found with id: " + calendarId
            );
        }

        return timeSlotRepository
                .findByCalendarIdOrderByStartTimeAsc(calendarId)
                .stream()
                .map(timeSlotMapper::toResponse)
                .toList();
    }

    @Override
    public TimeSlotResponse updateTimeSlot(
            Long id,
            UpdateTimeSlotRequest request) {

        validateTimeRange(request.startTime(), request.endTime());

        TimeSlot timeSlot = findTimeSlotById(id);

        if (timeSlot.getStatus() == SlotStatus.BOOKED) {
            throw new IllegalStateException(
                    "A booked time slot cannot be modified"
            );
        }

        Long calendarId = timeSlot.getCalendar().getId();

        boolean overlaps =
                timeSlotRepository.existsOverlappingSlotExcludingId(
                        timeSlot.getCalendar().getId(),
                        timeSlot.getId(),
                        request.startTime(),
                        request.endTime()
                );

        if (overlaps) {
            throw new OverlappingTimeSlotException(
                    "The updated time slot overlaps an existing slot"
            );
        }

        timeSlot.setStartTime(request.startTime());
        timeSlot.setEndTime(request.endTime());
        timeSlot.setUpdatedAt(OffsetDateTime.now());

        TimeSlot updatedTimeSlot =
                timeSlotRepository.save(timeSlot);

        return timeSlotMapper.toResponse(updatedTimeSlot);
    }

    @Override
    public void deleteTimeSlot(Long id) {

        TimeSlot timeSlot = findTimeSlotById(id);

        if (timeSlot.getStatus() == SlotStatus.BOOKED) {
            throw new IllegalStateException(
                    "A booked time slot cannot be deleted"
            );
        }

        timeSlotRepository.delete(timeSlot);
    }

    private TimeSlot findTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() ->
                        new TimeSlotNotFoundException(
                                "Time slot not found with id: " + id
                        )
                );
    }

    private void validateTimeRange(
            OffsetDateTime startTime,
            OffsetDateTime endTime) {

        if (!endTime.isAfter(startTime)) {
            throw new InvalidTimeRangeException(
                    "End time must be after start time"
            );
        }
    }
}