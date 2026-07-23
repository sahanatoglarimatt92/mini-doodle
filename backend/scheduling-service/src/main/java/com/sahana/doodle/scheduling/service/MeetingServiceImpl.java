package com.sahana.doodle.scheduling.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahana.doodle.scheduling.dto.AddParticipantsRequest;
import com.sahana.doodle.scheduling.dto.CreateMeetingRequest;
import com.sahana.doodle.scheduling.dto.MeetingResponse;
import com.sahana.doodle.scheduling.dto.ParticipantResponse;
import com.sahana.doodle.scheduling.exception.InvalidMeetingTimeSlotException;
import com.sahana.doodle.scheduling.exception.MeetingNotFoundException;
import com.sahana.doodle.scheduling.exception.ParticipantAlreadyAddedException;
import com.sahana.doodle.scheduling.exception.ParticipantNotFoundInMeetingException;
import com.sahana.doodle.scheduling.exception.ParticipantUnavailableException;
import com.sahana.doodle.scheduling.exception.TimeSlotAlreadyBookedException;
import com.sahana.doodle.scheduling.exception.TimeSlotNotFoundException;
import com.sahana.doodle.scheduling.exception.TimeSlotUnavailableException;
import com.sahana.doodle.scheduling.exception.UserNotFoundException;
import com.sahana.doodle.scheduling.mapper.MeetingMapper;
import com.sahana.doodle.scheduling.model.Meeting;
import com.sahana.doodle.scheduling.model.SlotStatus;
import com.sahana.doodle.scheduling.model.TimeSlot;
import com.sahana.doodle.scheduling.model.User;
import com.sahana.doodle.scheduling.repository.MeetingRepository;
import com.sahana.doodle.scheduling.repository.TimeSlotRepository;
import com.sahana.doodle.scheduling.repository.UserRepository;

@Service
@Transactional
public class MeetingServiceImpl implements MeetingService {

	private final MeetingRepository meetingRepository;
	private final TimeSlotRepository timeSlotRepository;
	private final UserRepository userRepository;
	private final MeetingMapper meetingMapper;

	public MeetingServiceImpl(
			MeetingRepository meetingRepository,
			TimeSlotRepository timeSlotRepository,
			UserRepository userRepository,
			MeetingMapper meetingMapper) {
		this.meetingRepository = meetingRepository;
		this.timeSlotRepository = timeSlotRepository;
		this.userRepository = userRepository;
		this.meetingMapper = meetingMapper;
	}
	
	public List<MeetingResponse> getAllMeetings() {
	    return meetingRepository.findAll()
	            .stream()
	            .map(meetingMapper::toResponse)
	            .toList();
	}

	@Override
	public MeetingResponse createMeeting(CreateMeetingRequest request) {

		TimeSlot timeSlot = timeSlotRepository.findById(request.timeSlotId())
				.orElseThrow(() -> new TimeSlotNotFoundException(
						"Time slot not found with id: " + request.timeSlotId()
						));

		if (timeSlot.getStatus() == SlotStatus.BOOKED
				|| meetingRepository.existsByTimeSlotId(request.timeSlotId())) {
			throw new TimeSlotAlreadyBookedException(
					"Time slot is already booked: " + request.timeSlotId()
					);
		}

		User organizer = userRepository.findById(request.organizerId())
				.orElseThrow(() -> new UserNotFoundException(
						"User not found with id: " + request.organizerId()
						));

		OffsetDateTime now = OffsetDateTime.now();

		Meeting meeting = new Meeting(
				timeSlot,
				organizer,
				request.title().trim(),
				normalizeDescription(request.description()),
				now,
				now
				);

		/*
		 * Both operations are part of the same transaction.
		 * If saving the meeting fails, the slot status change is rolled back.
		 */
		timeSlot.setMeeting(meeting);
		timeSlot.setStatus(SlotStatus.BOOKED);
		timeSlot.setUpdatedAt(now);

		Meeting savedMeeting = meetingRepository.save(meeting);
		timeSlotRepository.save(timeSlot);

		return meetingMapper.toResponse(savedMeeting);
	}

	@Override
	@Transactional(readOnly = true)
	public MeetingResponse getMeetingById(Long id) {
		return meetingMapper.toResponse(findMeetingById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public MeetingResponse getMeetingByTimeSlotId(Long timeSlotId) {

		Meeting meeting = meetingRepository.findByTimeSlotId(timeSlotId)
				.orElseThrow(() -> new MeetingNotFoundException(
						"Meeting not found for time slot id: " + timeSlotId
						));

		return meetingMapper.toResponse(meeting);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MeetingResponse> getMeetingsByOrganizerId(Long organizerId) {

		if (!userRepository.existsById(organizerId)) {
			throw new UserNotFoundException(
					"User not found with id: " + organizerId
					);
		}

		return meetingRepository
				.findByOrganizerIdOrderByTimeSlotStartTimeAsc(organizerId)
				.stream()
				.map(meetingMapper::toResponse)
				.toList();
	}

	@Override
	public void cancelMeeting(Long id) {

		Meeting meeting = meetingRepository.findById(id)
				.orElseThrow(() ->
				new MeetingNotFoundException(
						"Meeting not found with id: " + id
						)
						);

		TimeSlot timeSlot = meeting.getTimeSlot();

		// Clear the Java relationship on both sides
		timeSlot.setMeeting(null);
		meeting.setTimeSlot(null);

		// Delete and immediately execute the SQL
		meetingRepository.delete(meeting);
		meetingRepository.flush();

		// Make the slot available again
		timeSlot.setStatus(SlotStatus.FREE);
		timeSlot.setUpdatedAt(OffsetDateTime.now());

		timeSlotRepository.save(timeSlot);
	}

	private Meeting findMeetingById(Long id) {
		return meetingRepository.findById(id)
				.orElseThrow(() -> new MeetingNotFoundException(
						"Meeting not found with id: " + id
						));
	}

	private String normalizeDescription(String description) {
		if (description == null || description.isBlank()) {
			return null;
		}

		return description.trim();
	}

	@Override
	@Transactional
	public MeetingResponse addParticipants(
			Long meetingId,
			AddParticipantsRequest request) {

		Meeting meeting = meetingRepository.findById(meetingId)
				.orElseThrow(() ->
				new MeetingNotFoundException(
						"Meeting not found with id: " + meetingId
						)
						);

		OffsetDateTime startTime =
				meeting.getTimeSlot().getStartTime();

		OffsetDateTime endTime =
				meeting.getTimeSlot().getEndTime();

		for (Long participantId : request.participantIds()) {

			User participant = userRepository.findById(participantId)
					.orElseThrow(() ->
					new UserNotFoundException(
							"User not found with id: " + participantId
							)
							);

			if (meeting.getParticipants().contains(participant)) {
				throw new ParticipantAlreadyAddedException(
						"User " + participantId +
						" is already a participant in this meeting"
						);
			}

			boolean unavailable =
					meetingRepository.existsOverlappingMeetingForUser(
							participantId,
							meetingId,
							startTime,
							endTime
							);

			if (unavailable) {
				throw new ParticipantUnavailableException(
						"User " + participantId +
						" is unavailable during the requested meeting time"
						);
			}

			meeting.getParticipants().add(participant);
		}

		Meeting savedMeeting = meetingRepository.save(meeting);

		return meetingMapper.toResponse(savedMeeting);
	}

	@Override
	public MeetingResponse removeParticipant(
			Long meetingId,
			Long userId) {

		Meeting meeting = findMeetingById(meetingId);

		User participant = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(
						"User not found with id: " + userId
						));

		boolean participantExists = meeting.getParticipants()
				.stream()
				.anyMatch(existing ->
				existing.getId().equals(userId)
						);

		if (!participantExists) {
			throw new ParticipantNotFoundInMeetingException(
					"User " + userId
					+ " is not a participant of meeting "
					+ meetingId
					);
		}

		meeting.removeParticipant(participant);
		meeting.setUpdatedAt(OffsetDateTime.now());

		Meeting savedMeeting = meetingRepository.save(meeting);

		return meetingMapper.toResponse(savedMeeting);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ParticipantResponse> getParticipants(Long meetingId) {

		Meeting meeting = findMeetingById(meetingId);

		return meeting.getParticipants()
				.stream()
				.map(user -> new ParticipantResponse(
						user.getId(),
						user.getName(),
						user.getEmail()
						))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MeetingResponse> getMeetingsByParticipantId(
			Long userId) {

		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException(
					"User not found with id: " + userId
					);
		}

		return meetingRepository
				.findMeetingsByParticipantId(userId)
				.stream()
				.map(meetingMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public void rescheduleMeeting(
			Long meetingId,
			Long newTimeSlotId) {

		Meeting meeting = meetingRepository.findById(meetingId)
				.orElseThrow(() ->
				new MeetingNotFoundException(
						"Meeting not found with id: " + meetingId
						)
						);

		TimeSlot newTimeSlot = timeSlotRepository.findById(newTimeSlotId)
				.orElseThrow(() ->
				new TimeSlotNotFoundException(
						"Time slot not found with id: " + newTimeSlotId
						)
						);

		TimeSlot oldTimeSlot = meeting.getTimeSlot();

		validateNewTimeSlot(meeting, newTimeSlot);

		validateMeetingUsersAvailability(
				meeting,
				newTimeSlot.getStartTime(),
				newTimeSlot.getEndTime()
				);

		oldTimeSlot.setMeeting(null);
		oldTimeSlot.setStatus(SlotStatus.FREE);

		meeting.setTimeSlot(newTimeSlot);

		newTimeSlot.setMeeting(meeting);
		newTimeSlot.setStatus(SlotStatus.BOOKED);

		timeSlotRepository.save(oldTimeSlot);
		meetingRepository.save(meeting);
		timeSlotRepository.save(newTimeSlot);
	}

	private void validateNewTimeSlot(
			Meeting meeting,
			TimeSlot newTimeSlot) {

		if (meeting.getTimeSlot().getId().equals(newTimeSlot.getId())) {
			throw new InvalidMeetingTimeSlotException(
					"The meeting is already scheduled in this time slot"
					);
		}

		if (newTimeSlot.getStatus() != SlotStatus.FREE) {
			throw new TimeSlotUnavailableException(
					"The selected time slot is not free"
					);
		}

		Long organizerId = meeting.getOrganizer().getId();

		Long calendarOwnerId =
				newTimeSlot.getCalendar()
				.getUser()
				.getId();

		if (!calendarOwnerId.equals(organizerId)) {
			throw new InvalidMeetingTimeSlotException(
					"The selected time slot does not belong to the meeting organizer"
					);
		}
	}

	private void validateMeetingUsersAvailability(
			Meeting meeting,
			OffsetDateTime startTime,
			OffsetDateTime endTime) {

		Long meetingId = meeting.getId();

		validateUserAvailability(
				meeting.getOrganizer().getId(),
				meetingId,
				startTime,
				endTime
				);

		for (User participant : meeting.getParticipants()) {
			validateUserAvailability(
					participant.getId(),
					meetingId,
					startTime,
					endTime
					);
		}
	}

	private void validateUserAvailability(
			Long userId,
			Long meetingId,
			OffsetDateTime startTime,
			OffsetDateTime endTime) {

		boolean unavailable =
				meetingRepository.existsOverlappingMeetingForUser(
						userId,
						meetingId,
						startTime,
						endTime
						);

		if (unavailable) {
			throw new ParticipantUnavailableException(
					"User " + userId +
					" is unavailable during the requested meeting time"
					);
		}
	}
}