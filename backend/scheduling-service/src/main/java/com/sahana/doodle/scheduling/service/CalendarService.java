package com.sahana.doodle.scheduling.service;

import java.util.List;

import com.sahana.doodle.scheduling.dto.CalendarResponse;
import com.sahana.doodle.scheduling.dto.CreateCalendarRequest;
import com.sahana.doodle.scheduling.dto.UpdateCalendarRequest;

public interface  CalendarService {
	CalendarResponse createCalendar(CreateCalendarRequest request);

	CalendarResponse getCalendarById(Long id);

	CalendarResponse getCalendarByUserId(Long userId);

	List<CalendarResponse> getAllCalendars();

	CalendarResponse updateCalendar(
			Long id,
			UpdateCalendarRequest request
			);

	void deleteCalendar(Long id);
}
