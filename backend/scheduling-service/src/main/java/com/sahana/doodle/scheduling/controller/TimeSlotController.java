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

import com.sahana.doodle.scheduling.dto.CreateTimeSlotRequest;
import com.sahana.doodle.scheduling.dto.TimeSlotResponse;
import com.sahana.doodle.scheduling.dto.UpdateTimeSlotRequest;
import com.sahana.doodle.scheduling.service.TimeSlotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/time-slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @PostMapping
    public ResponseEntity<TimeSlotResponse> createTimeSlot(
            @Valid @RequestBody CreateTimeSlotRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(timeSlotService.createTimeSlot(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> getTimeSlotById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                timeSlotService.getTimeSlotById(id)
        );
    }

    @GetMapping("/calendar/{calendarId}")
    public ResponseEntity<List<TimeSlotResponse>>
            getTimeSlotsByCalendarId(
                    @PathVariable Long calendarId) {

        return ResponseEntity.ok(
                timeSlotService.getTimeSlotsByCalendarId(calendarId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> updateTimeSlot(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTimeSlotRequest request) {

        return ResponseEntity.ok(
                timeSlotService.updateTimeSlot(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(
            @PathVariable Long id) {

        timeSlotService.deleteTimeSlot(id);

        return ResponseEntity.noContent().build();
    }
}
