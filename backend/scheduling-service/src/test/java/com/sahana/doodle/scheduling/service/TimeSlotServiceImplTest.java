package com.sahana.doodle.scheduling.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sahana.doodle.scheduling.dto.CreateTimeSlotRequest;
import com.sahana.doodle.scheduling.dto.TimeSlotResponse;
import com.sahana.doodle.scheduling.exception.OverlappingTimeSlotException;
import com.sahana.doodle.scheduling.mapper.TimeSlotMapper;
import com.sahana.doodle.scheduling.model.Calendar;
import com.sahana.doodle.scheduling.model.SlotStatus;
import com.sahana.doodle.scheduling.model.TimeSlot;
import com.sahana.doodle.scheduling.model.User;
import com.sahana.doodle.scheduling.repository.CalendarRepository;
import com.sahana.doodle.scheduling.repository.TimeSlotRepository;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceImplTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private TimeSlotMapper timeSlotMapper;

    private TimeSlotServiceImpl service;

    private Calendar calendar;
    private TimeSlot timeSlot;
    private TimeSlotResponse response;

    @BeforeEach
    void setUp() {

        service = new TimeSlotServiceImpl(
                timeSlotRepository,
                calendarRepository,
                timeSlotMapper);

        User user = new User(
                "Sahana",
                "sahana@test.com",
                "UTC");

        user.setId(1L);

        calendar = new Calendar(
                user,
                "My Calendar",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        calendar.setId(1L);

        timeSlot = new TimeSlot(
                calendar,
                OffsetDateTime.parse("2026-07-23T09:00:00Z"),
                OffsetDateTime.parse("2026-07-23T10:00:00Z"),
                SlotStatus.FREE,
                OffsetDateTime.now(),
                OffsetDateTime.now());

        timeSlot.setId(10L);

        response = new TimeSlotResponse(
                10L,
                1L,
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                SlotStatus.FREE,
                OffsetDateTime.now(),
                OffsetDateTime.now());
    }

    @Test
    void shouldCreateTimeSlotSuccessfully() {

        CreateTimeSlotRequest request =
                new CreateTimeSlotRequest(
                        1L,
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime());

        when(calendarRepository.findById(1L))
                .thenReturn(Optional.of(calendar));

        when(timeSlotRepository.existsOverlappingSlot(
                1L,
                timeSlot.getStartTime(),
                timeSlot.getEndTime()))
                .thenReturn(false);

        when(timeSlotRepository.save(any(TimeSlot.class)))
                .thenReturn(timeSlot);

        when(timeSlotMapper.toResponse(timeSlot))
                .thenReturn(response);

        TimeSlotResponse result =
                service.createTimeSlot(request);

        assertEquals(SlotStatus.FREE, result.status());
        assertEquals(1L, result.calendarId());

        verify(timeSlotRepository).save(any(TimeSlot.class));
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotOverlaps() {

        CreateTimeSlotRequest request =
                new CreateTimeSlotRequest(
                        1L,
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime());

        when(calendarRepository.findById(1L))
                .thenReturn(Optional.of(calendar));

        when(timeSlotRepository.existsOverlappingSlot(
                1L,
                timeSlot.getStartTime(),
                timeSlot.getEndTime()))
                .thenReturn(true);

        assertThrows(
                OverlappingTimeSlotException.class,
                () -> service.createTimeSlot(request));
    }
}