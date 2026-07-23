package com.sahana.doodle.scheduling.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.sahana.doodle.scheduling.dto.AvailabilityResponse;
import com.sahana.doodle.scheduling.dto.TimeRangeResponse;
import com.sahana.doodle.scheduling.exception.UserNotFoundException;
import com.sahana.doodle.scheduling.model.User;
import com.sahana.doodle.scheduling.repository.MeetingRepository;
import com.sahana.doodle.scheduling.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserRepository userRepository;

    private AvailabilityServiceImpl availabilityService;

    private User user;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    @BeforeEach
    void setUp() {

        availabilityService = new AvailabilityServiceImpl(
                meetingRepository,
                userRepository
        );

        user = new User(
                "Sahana",
                "sahana@test.com",
                "UTC"
        );

        user.setId(1L);

        startTime =
                OffsetDateTime.parse(
                        "2026-07-23T09:00:00Z"
                );

        endTime =
                OffsetDateTime.parse(
                        "2026-07-23T17:00:00Z"
                );
    }

    @Test
    void shouldReturnWholeTimeRangeWhenNoMeetingsExist() {

        List<Long> userIds = List.of(1L);

        when(meetingRepository.findBusyMeetingsForUsers(
                userIds,
                startTime,
                endTime
        )).thenReturn(List.of());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        AvailabilityResponse response =
                availabilityService.getAvailability(
                        userIds,
                        startTime,
                        endTime
                );

        assertEquals(startTime, response.searchStartTime());
        assertEquals(endTime, response.searchEndTime());

        assertEquals(1, response.users().size());
        assertEquals(1L, response.users().getFirst().userId());
        assertEquals("Sahana", response.users().getFirst().userName());

        assertTrue(
                response.users()
                        .getFirst()
                        .busySlots()
                        .isEmpty()
        );

        assertEquals(1, response.commonFreeSlots().size());

        TimeRangeResponse freeSlot =
                response.commonFreeSlots().getFirst();

        assertEquals(startTime, freeSlot.startTime());
        assertEquals(endTime, freeSlot.endTime());

        verify(meetingRepository)
                .findBusyMeetingsForUsers(
                        userIds,
                        startTime,
                        endTime
                );

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {

        List<Long> userIds = List.of(99L);

        when(meetingRepository.findBusyMeetingsForUsers(
                userIds,
                startTime,
                endTime
        )).thenReturn(List.of());

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(
                        UserNotFoundException.class,
                        () -> availabilityService.getAvailability(
                                userIds,
                                startTime,
                                endTime
                        )
                );

        assertEquals(
                "User not found with id: 99",
                exception.getMessage()
        );

        verify(userRepository).findById(99L);
    }
}