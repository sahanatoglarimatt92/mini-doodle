package com.sahana.doodle.scheduling.repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sahana.doodle.scheduling.model.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

	Optional<Meeting> findByTimeSlotId(Long timeSlotId);

	boolean existsByTimeSlotId(Long timeSlotId);

	List<Meeting> findByOrganizerIdOrderByTimeSlotStartTimeAsc(
			Long organizerId
			);

	@Query("""
			SELECT DISTINCT meeting
			FROM Meeting meeting
			JOIN meeting.participants participant
			JOIN FETCH meeting.timeSlot timeSlot
			JOIN FETCH meeting.organizer organizer
			WHERE participant.id = :participantId
			ORDER BY timeSlot.startTime ASC
			""")
	List<Meeting> findMeetingsByParticipantId(
			@Param("participantId") Long participantId
			);


	@Query("""
			SELECT COUNT(meeting) > 0
			FROM Meeting meeting
			JOIN meeting.participants participant
			JOIN meeting.timeSlot timeSlot
			WHERE participant.id = :participantId
			  AND timeSlot.startTime < :endTime
			  AND timeSlot.endTime > :startTime
			""")
	boolean existsOverlappingMeetingForParticipant(
			@Param("participantId") Long participantId,
			@Param("startTime") OffsetDateTime startTime,
			@Param("endTime") OffsetDateTime endTime
			);

	@Query("""
			SELECT COUNT(meeting) > 0
			FROM Meeting meeting
			JOIN meeting.participants participant
			WHERE participant.id = :participantId
			  AND meeting.id <> :meetingId
			  AND meeting.timeSlot.startTime < :endTime
			  AND meeting.timeSlot.endTime > :startTime
			""")
	boolean existsOverlappingMeetingForParticipant(
			@Param("participantId") Long participantId,
			@Param("meetingId") Long meetingId,
			@Param("startTime") OffsetDateTime startTime,
			@Param("endTime") OffsetDateTime endTime
			);

	@Query("""
			SELECT COUNT(meeting) > 0
			FROM Meeting meeting
			LEFT JOIN meeting.participants participant
			WHERE meeting.id <> :meetingId
			  AND (
			        participant.id = :userId
			        OR meeting.organizer.id = :userId
			      )
			  AND meeting.timeSlot.startTime < :endTime
			  AND meeting.timeSlot.endTime > :startTime
			""")
	boolean existsOverlappingMeetingForUser(
			@Param("userId") Long userId,
			@Param("meetingId") Long meetingId,
			@Param("startTime") OffsetDateTime startTime,
			@Param("endTime") OffsetDateTime endTime
			);

	@Query("""
			SELECT DISTINCT meeting
			FROM Meeting meeting
			JOIN FETCH meeting.timeSlot timeSlot
			JOIN FETCH meeting.organizer organizer
			LEFT JOIN meeting.participants matchingParticipant
			LEFT JOIN FETCH meeting.participants allParticipants
			WHERE (
			        organizer.id IN :userIds
			        OR matchingParticipant.id IN :userIds
			      )
			  AND timeSlot.startTime < :endTime
			  AND timeSlot.endTime > :startTime
			ORDER BY timeSlot.startTime ASC
			""")
	List<Meeting> findBusyMeetingsForUsers(
			@Param("userIds") Collection<Long> userIds,
			@Param("startTime") OffsetDateTime startTime,
			@Param("endTime") OffsetDateTime endTime
			);
}
