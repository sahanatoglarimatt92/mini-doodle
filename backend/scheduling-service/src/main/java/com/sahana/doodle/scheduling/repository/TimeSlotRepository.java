package com.sahana.doodle.scheduling.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sahana.doodle.scheduling.model.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

	List<TimeSlot> findByCalendarIdOrderByStartTimeAsc(Long calendarId);


	@Query("""
			SELECT COUNT(timeSlot) > 0
			FROM TimeSlot timeSlot
			WHERE timeSlot.calendar.id = :calendarId
			  AND timeSlot.id <> :timeSlotId
			  AND timeSlot.startTime < :endTime
			  AND timeSlot.endTime > :startTime
			""")
	boolean existsOverlappingSlotExcludingId(
			@Param("calendarId") Long calendarId,
			@Param("timeSlotId") Long timeSlotId,
			@Param("startTime") OffsetDateTime startTime,
			@Param("endTime") OffsetDateTime endTime
			);


	@Query("""
			SELECT COUNT(timeSlot) > 0
			FROM TimeSlot timeSlot
			WHERE timeSlot.calendar.id = :calendarId
			  AND timeSlot.startTime < :endTime
			  AND timeSlot.endTime > :startTime
			""")
	boolean existsOverlappingSlot(
			@Param("calendarId") Long calendarId,
			@Param("startTime") OffsetDateTime startTime,
			@Param("endTime") OffsetDateTime endTime
			);
}
