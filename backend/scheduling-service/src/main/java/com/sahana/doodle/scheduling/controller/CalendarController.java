package com.sahana.doodle.scheduling.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahana.doodle.scheduling.dto.CalendarResponse;
import com.sahana.doodle.scheduling.dto.CreateCalendarRequest;
import com.sahana.doodle.scheduling.dto.UpdateCalendarRequest;
import com.sahana.doodle.scheduling.service.CalendarService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calendars")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping
    public ResponseEntity<CalendarResponse> createCalendar(
            @Valid @RequestBody CreateCalendarRequest request) {

        CalendarResponse response =
                calendarService.createCalendar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarResponse> getCalendarById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                calendarService.getCalendarById(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CalendarResponse> getCalendarByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                calendarService.getCalendarByUserId(userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<CalendarResponse>> getAllCalendars() {

        return ResponseEntity.ok(
                calendarService.getAllCalendars()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarResponse> updateCalendar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCalendarRequest request) {

        return ResponseEntity.ok(
                calendarService.updateCalendar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendar(
            @PathVariable Long id) {

        calendarService.deleteCalendar(id);

        return ResponseEntity.noContent().build();
    }
}