package com.sahana.doodle.scheduling.service;

import java.util.List;

import com.sahana.doodle.scheduling.dto.AddParticipantsRequest;
import com.sahana.doodle.scheduling.dto.CreateMeetingRequest;
import com.sahana.doodle.scheduling.dto.MeetingResponse;
import com.sahana.doodle.scheduling.dto.ParticipantResponse;

public interface MeetingService {

	MeetingResponse createMeeting(CreateMeetingRequest request);

	MeetingResponse getMeetingById(Long id);

	MeetingResponse getMeetingByTimeSlotId(Long timeSlotId);

	List<MeetingResponse> getMeetingsByOrganizerId(Long organizerId);

	void cancelMeeting(Long id);
	
	public List<MeetingResponse> getAllMeetings();

	MeetingResponse addParticipants(
			Long meetingId,
			AddParticipantsRequest request
			);

	MeetingResponse removeParticipant(
			Long meetingId,
			Long userId
			);

	List<ParticipantResponse> getParticipants(
			Long meetingId
			);

	List<MeetingResponse> getMeetingsByParticipantId(
			Long userId
			);

	void rescheduleMeeting(
			Long meetingId,
			Long newTimeSlotId
			);
}