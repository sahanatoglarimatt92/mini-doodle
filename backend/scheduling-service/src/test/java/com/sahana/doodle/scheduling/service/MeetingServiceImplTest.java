package com.sahana.doodle.scheduling.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sahana.doodle.scheduling.dto.CreateMeetingRequest;
import com.sahana.doodle.scheduling.dto.MeetingResponse;
import com.sahana.doodle.scheduling.exception.TimeSlotAlreadyBookedException;
import com.sahana.doodle.scheduling.mapper.MeetingMapper;
import com.sahana.doodle.scheduling.model.Calendar;
import com.sahana.doodle.scheduling.model.Meeting;
import com.sahana.doodle.scheduling.model.SlotStatus;
import com.sahana.doodle.scheduling.model.TimeSlot;
import com.sahana.doodle.scheduling.model.User;
import com.sahana.doodle.scheduling.repository.MeetingRepository;
import com.sahana.doodle.scheduling.repository.TimeSlotRepository;
import com.sahana.doodle.scheduling.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeetingMapper meetingMapper;

    private MeetingServiceImpl meetingService;

    private User organizer;
    private TimeSlot timeSlot;
    private Meeting meeting;
    private MeetingResponse response;

    @BeforeEach
    void setup() {

        meetingService = new MeetingServiceImpl(
                meetingRepository,
                timeSlotRepository,
                userRepository,
                meetingMapper);

        organizer = new User("Sahana",
                "sahana@test.com",
                "UTC");
        organizer.setId(1L);

        Calendar calendar = new Calendar(
                organizer,
                "My Calendar",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        timeSlot = new TimeSlot(
                calendar,
                OffsetDateTime.parse("2026-07-23T09:00:00Z"),
                OffsetDateTime.parse("2026-07-23T10:00:00Z"),
                SlotStatus.FREE,
                OffsetDateTime.now(),
                OffsetDateTime.now());

        timeSlot.setId(10L);

        meeting = new Meeting(
                timeSlot,
                organizer,
                "Sprint Planning",
                "Discussion",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        response = new MeetingResponse(
                1L,
                10L,
                1L,
                "Sahana",
                "Sprint Planning",
                "Discussion",
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                List.of(),
                OffsetDateTime.now(),
                OffsetDateTime.now());
    }

    @Test
    void shouldCreateMeetingSuccessfully() {

        CreateMeetingRequest request =
                new CreateMeetingRequest(
                        10L,
                        1L,
                        "Sprint Planning",
                        "Discussion");

        when(timeSlotRepository.findById(10L))
                .thenReturn(Optional.of(timeSlot));

        when(meetingRepository.existsByTimeSlotId(10L))
                .thenReturn(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(organizer));

        when(meetingRepository.save(any(Meeting.class)))
                .thenReturn(meeting);

        when(meetingMapper.toResponse(any(Meeting.class)))
                .thenReturn(response);

        MeetingResponse result =
                meetingService.createMeeting(request);

        assertEquals("Sprint Planning", result.title());
        assertEquals(SlotStatus.BOOKED, timeSlot.getStatus());

        verify(meetingRepository).save(any(Meeting.class));
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotAlreadyBooked() {

        timeSlot.setStatus(SlotStatus.BOOKED);

        CreateMeetingRequest request =
                new CreateMeetingRequest(
                        10L,
                        1L,
                        "Sprint Planning",
                        null);

        when(timeSlotRepository.findById(10L))
                .thenReturn(Optional.of(timeSlot));

        assertThrows(
                TimeSlotAlreadyBookedException.class,
                () -> meetingService.createMeeting(request));
    }

    @Test
    void shouldCancelMeetingSuccessfully() {

        timeSlot.setMeeting(meeting);
        timeSlot.setStatus(SlotStatus.BOOKED);

        when(meetingRepository.findById(1L))
                .thenReturn(Optional.of(meeting));

        meetingService.cancelMeeting(1L);

        assertEquals(SlotStatus.FREE, timeSlot.getStatus());

        verify(meetingRepository).delete(meeting);
        verify(meetingRepository).flush();
        verify(timeSlotRepository).save(timeSlot);
    }
}
