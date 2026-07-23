package com.sahana.doodle.scheduling.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahana.doodle.scheduling.dto.AvailabilityResponse;
import com.sahana.doodle.scheduling.dto.TimeRangeResponse;
import com.sahana.doodle.scheduling.dto.UserAvailabilityResponse;
import com.sahana.doodle.scheduling.exception.InvalidAvailabilityRequestException;
import com.sahana.doodle.scheduling.exception.UserNotFoundException;
import com.sahana.doodle.scheduling.model.Meeting;
import com.sahana.doodle.scheduling.model.User;
import com.sahana.doodle.scheduling.repository.MeetingRepository;
import com.sahana.doodle.scheduling.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AvailabilityServiceImpl implements AvailabilityService {

	private final MeetingRepository meetingRepository;
	private final UserRepository userRepository;

	public AvailabilityServiceImpl(
			MeetingRepository meetingRepository,
			UserRepository userRepository) {

		this.meetingRepository = meetingRepository;
		this.userRepository = userRepository;
	}

	@Override
	public AvailabilityResponse getAvailability(
			List<Long> userIds,
			OffsetDateTime startTime,
			OffsetDateTime endTime) {

		validateRequest(userIds, startTime, endTime);

		List<Meeting> meetings =
				meetingRepository.findBusyMeetingsForUsers(
						userIds,
						startTime,
						endTime
						);

		List<UserAvailabilityResponse> userAvailability =
				buildUserAvailability(
						userIds,
						meetings,
						startTime,
						endTime
						);

		List<TimeRangeResponse> commonFreeSlots =
				calculateCommonFreeSlots(
						meetings,
						startTime,
						endTime
						);

		return new AvailabilityResponse(
				startTime,
				endTime,
				userAvailability,
				commonFreeSlots
				);
	}

	private void validateRequest(
			List<Long> userIds,
			OffsetDateTime startTime,
			OffsetDateTime endTime) {

		if (userIds == null || userIds.isEmpty()) {
			throw new InvalidAvailabilityRequestException(
					"At least one user ID must be provided"
					);
		}

		if (startTime == null || endTime == null) {
			throw new InvalidAvailabilityRequestException(
					"Start time and end time must be provided"
					);
		}

		if (!endTime.isAfter(startTime)) {
			throw new InvalidAvailabilityRequestException(
					"End time must be after start time"
					);
		}
	}

	private List<UserAvailabilityResponse> buildUserAvailability(
			List<Long> userIds,
			List<Meeting> meetings,
			OffsetDateTime searchStartTime,
			OffsetDateTime searchEndTime) {

		List<UserAvailabilityResponse> responses = new ArrayList<>();

		for (Long userId : userIds) {

			User user = userRepository.findById(userId)
					.orElseThrow(() ->
					new UserNotFoundException(
							"User not found with id: " + userId
							)
							);

			List<TimeRangeResponse> busySlots = meetings.stream()
					.filter(meeting -> isUserPartOfMeeting(meeting, userId))
					.map(meeting -> toClippedTimeRange(
							meeting,
							searchStartTime,
							searchEndTime
							))
					.sorted(Comparator.comparing(
							TimeRangeResponse::startTime
							))
					.toList();

			List<TimeRangeResponse> mergedBusySlots =
					mergeTimeRanges(busySlots);

			responses.add(
					new UserAvailabilityResponse(
							user.getId(),
							user.getName(),
							mergedBusySlots
							)
					);
		}

		return responses;
	}

	private boolean isUserPartOfMeeting(
			Meeting meeting,
			Long userId) {

		boolean isOrganizer =
				meeting.getOrganizer().getId().equals(userId);

		boolean isParticipant =
				meeting.getParticipants()
				.stream()
				.anyMatch(participant ->
				participant.getId().equals(userId)
						);

		return isOrganizer || isParticipant;
	}

	private TimeRangeResponse toClippedTimeRange(
			Meeting meeting,
			OffsetDateTime searchStartTime,
			OffsetDateTime searchEndTime) {

		OffsetDateTime meetingStart =
				meeting.getTimeSlot().getStartTime();

		OffsetDateTime meetingEnd =
				meeting.getTimeSlot().getEndTime();

		OffsetDateTime clippedStart =
				meetingStart.isBefore(searchStartTime)
				? searchStartTime
						: meetingStart;

		OffsetDateTime clippedEnd =
				meetingEnd.isAfter(searchEndTime)
				? searchEndTime
						: meetingEnd;

		return new TimeRangeResponse(
				clippedStart,
				clippedEnd
				);
	}

	private List<TimeRangeResponse> mergeTimeRanges(
			List<TimeRangeResponse> ranges) {

		if (ranges.isEmpty()) {
			return List.of();
		}

		List<TimeRangeResponse> sortedRanges =
				ranges.stream()
				.sorted(Comparator.comparing(
						TimeRangeResponse::startTime
						))
				.toList();

		List<TimeRangeResponse> merged = new ArrayList<>();

		TimeRangeResponse current = sortedRanges.getFirst();

		for (int index = 1; index < sortedRanges.size(); index++) {

			TimeRangeResponse next = sortedRanges.get(index);

			boolean overlapsOrTouches =
					!next.startTime().isAfter(current.endTime());

			if (overlapsOrTouches) {

				OffsetDateTime mergedEnd =
						next.endTime().isAfter(current.endTime())
						? next.endTime()
								: current.endTime();

				current = new TimeRangeResponse(
						current.startTime(),
						mergedEnd
						);

			} else {
				merged.add(current);
				current = next;
			}
		}

		merged.add(current);

		return merged;
	}

	private List<TimeRangeResponse> calculateCommonFreeSlots(
			List<Meeting> meetings,
			OffsetDateTime searchStartTime,
			OffsetDateTime searchEndTime) {

		List<TimeRangeResponse> allBusyRanges = meetings.stream()
				.map(meeting -> toClippedTimeRange(
						meeting,
						searchStartTime,
						searchEndTime
						))
				.sorted(Comparator.comparing(
						TimeRangeResponse::startTime
						))
				.toList();

		List<TimeRangeResponse> mergedBusyRanges =
				mergeTimeRanges(allBusyRanges);

		return invertBusyRanges(
				mergedBusyRanges,
				searchStartTime,
				searchEndTime
				);
	}

	private List<TimeRangeResponse> invertBusyRanges(
			List<TimeRangeResponse> busyRanges,
			OffsetDateTime searchStartTime,
			OffsetDateTime searchEndTime) {

		if (busyRanges.isEmpty()) {
			return List.of(
					new TimeRangeResponse(
							searchStartTime,
							searchEndTime
							)
					);
		}

		List<TimeRangeResponse> freeRanges = new ArrayList<>();

		OffsetDateTime currentTime = searchStartTime;

		for (TimeRangeResponse busyRange : busyRanges) {

			if (currentTime.isBefore(busyRange.startTime())) {
				freeRanges.add(
						new TimeRangeResponse(
								currentTime,
								busyRange.startTime()
								)
						);
			}

			if (busyRange.endTime().isAfter(currentTime)) {
				currentTime = busyRange.endTime();
			}
		}

		if (currentTime.isBefore(searchEndTime)) {
			freeRanges.add(
					new TimeRangeResponse(
							currentTime,
							searchEndTime
							)
					);
		}

		return freeRanges;
	}
}