package com.sahana.doodle.scheduling.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sahana.doodle.scheduling.dto.MeetingResponse;
import com.sahana.doodle.scheduling.dto.ParticipantResponse;
import com.sahana.doodle.scheduling.model.Meeting;

@Component
public class MeetingMapper {

	public MeetingResponse toResponse(Meeting meeting) {
		List<ParticipantResponse> participants =
				meeting.getParticipants()
				.stream()
				.map(user -> new ParticipantResponse(
						user.getId(),
						user.getName(),
						user.getEmail()
						))
				.toList();

		return new MeetingResponse(
				meeting.getId(),
				meeting.getTimeSlot().getId(),
				meeting.getOrganizer().getId(),
				meeting.getOrganizer().getName(),
				meeting.getTitle(),
				meeting.getDescription(),
				meeting.getTimeSlot().getStartTime(),
				meeting.getTimeSlot().getEndTime(),
				participants,
				meeting.getCreatedAt(),
				meeting.getUpdatedAt()
				);
	}
}
