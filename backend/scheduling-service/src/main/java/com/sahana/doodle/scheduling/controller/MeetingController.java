package com.sahana.doodle.scheduling.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahana.doodle.scheduling.dto.AddParticipantsRequest;
import com.sahana.doodle.scheduling.dto.CreateMeetingRequest;
import com.sahana.doodle.scheduling.dto.MeetingResponse;
import com.sahana.doodle.scheduling.dto.ParticipantResponse;
import com.sahana.doodle.scheduling.dto.RescheduleMeetingRequest;
import com.sahana.doodle.scheduling.service.MeetingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

	private final MeetingService meetingService;

	public MeetingController(MeetingService meetingService) {
		this.meetingService = meetingService;
	}

	@GetMapping
	public ResponseEntity<List<MeetingResponse>> getAllMeetings() {
		return ResponseEntity.ok(meetingService.getAllMeetings());
	}

	@PostMapping
	public ResponseEntity<MeetingResponse> createMeeting(
			@Valid @RequestBody CreateMeetingRequest request) {

		MeetingResponse response =
				meetingService.createMeeting(request);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<MeetingResponse> getMeetingById(
			@PathVariable Long id) {

		return ResponseEntity.ok(
				meetingService.getMeetingById(id)
				);
	}

	@GetMapping("/time-slot/{timeSlotId}")
	public ResponseEntity<MeetingResponse> getMeetingByTimeSlotId(
			@PathVariable Long timeSlotId) {

		return ResponseEntity.ok(
				meetingService.getMeetingByTimeSlotId(timeSlotId)
				);
	}

	@GetMapping("/organizer/{organizerId}")
	public ResponseEntity<List<MeetingResponse>> getMeetingsByOrganizerId(
			@PathVariable Long organizerId) {

		return ResponseEntity.ok(
				meetingService.getMeetingsByOrganizerId(organizerId)
				);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelMeeting(
			@PathVariable Long id) {

		meetingService.cancelMeeting(id);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{meetingId}/participants")
	public ResponseEntity<MeetingResponse> addParticipants(
			@PathVariable Long meetingId,
			@Valid @RequestBody AddParticipantsRequest request) {

		return ResponseEntity.ok(
				meetingService.addParticipants(meetingId, request)
				);
	}

	@DeleteMapping("/{meetingId}/participants/{userId}")
	public ResponseEntity<MeetingResponse> removeParticipant(
			@PathVariable Long meetingId,
			@PathVariable Long userId) {

		return ResponseEntity.ok(
				meetingService.removeParticipant(meetingId, userId)
				);
	}

	@GetMapping("/{meetingId}/participants")
	public ResponseEntity<List<ParticipantResponse>> getParticipants(
			@PathVariable Long meetingId) {

		return ResponseEntity.ok(
				meetingService.getParticipants(meetingId)
				);
	}

	@GetMapping("/participant/{userId}")
	public ResponseEntity<List<MeetingResponse>> getMeetingsByParticipant(
			@PathVariable Long userId) {

		return ResponseEntity.ok(
				meetingService.getMeetingsByParticipantId(userId)
				);
	}

	@PutMapping("/{meetingId}/reschedule")
	public ResponseEntity<Void> rescheduleMeeting(
			@PathVariable Long meetingId,
			@Valid @RequestBody RescheduleMeetingRequest request) {

		meetingService.rescheduleMeeting(
				meetingId,
				request.newTimeSlotId()
				);

		return ResponseEntity.noContent().build();
	}
}
