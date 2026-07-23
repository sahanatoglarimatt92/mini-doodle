package com.sahana.doodle.scheduling.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sahana.doodle.scheduling.dto.CalendarResponse;
import com.sahana.doodle.scheduling.dto.CreateCalendarRequest;
import com.sahana.doodle.scheduling.dto.UpdateCalendarRequest;
import com.sahana.doodle.scheduling.exception.CalendarAlreadyExistsException;
import com.sahana.doodle.scheduling.exception.CalendarNotFoundException;
import com.sahana.doodle.scheduling.exception.UserNotFoundException;
import com.sahana.doodle.scheduling.mapper.CalendarMapper;
import com.sahana.doodle.scheduling.model.Calendar;
import com.sahana.doodle.scheduling.model.User;
import com.sahana.doodle.scheduling.repository.CalendarRepository;
import com.sahana.doodle.scheduling.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final CalendarMapper calendarMapper;

    public CalendarServiceImpl(
            CalendarRepository calendarRepository,
            UserRepository userRepository,
            CalendarMapper calendarMapper) {
        this.calendarRepository = calendarRepository;
        this.userRepository = userRepository;
        this.calendarMapper = calendarMapper;
    }

    @Override
    public CalendarResponse createCalendar(CreateCalendarRequest request) {

        if (calendarRepository.existsByUserId(request.userId())) {
            throw new CalendarAlreadyExistsException(
                    "Calendar already exists for user id: "
                            + request.userId()
            );
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: "
                                        + request.userId()
                        )
                );

        OffsetDateTime now = OffsetDateTime.now();

        Calendar calendar = new Calendar(
                user,
                request.name().trim(),
                now,
                now
        );

        Calendar savedCalendar =
                calendarRepository.save(calendar);

        return calendarMapper.toResponse(savedCalendar);
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarResponse getCalendarById(Long id) {

        Calendar calendar = findCalendarById(id);

        return calendarMapper.toResponse(calendar);
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarResponse getCalendarByUserId(Long userId) {

        Calendar calendar = calendarRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CalendarNotFoundException(
                                "Calendar not found for user id: "
                                        + userId
                        )
                );

        return calendarMapper.toResponse(calendar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarResponse> getAllCalendars() {

        return calendarRepository.findAll()
                .stream()
                .map(calendarMapper::toResponse)
                .toList();
    }

    @Override
    public CalendarResponse updateCalendar(
            Long id,
            UpdateCalendarRequest request) {

        Calendar calendar = findCalendarById(id);

        calendar.setName(request.name().trim());
        calendar.setUpdatedAt(OffsetDateTime.now());

        Calendar updatedCalendar =
                calendarRepository.save(calendar);

        return calendarMapper.toResponse(updatedCalendar);
    }

    @Override
    public void deleteCalendar(Long id) {

        Calendar calendar = findCalendarById(id);

        calendarRepository.delete(calendar);
    }

    private Calendar findCalendarById(Long id) {

        return calendarRepository.findById(id)
                .orElseThrow(() ->
                        new CalendarNotFoundException(
                                "Calendar not found with id: " + id
                        )
                );
    }
}
