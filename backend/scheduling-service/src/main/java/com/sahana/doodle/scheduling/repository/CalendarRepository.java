package com.sahana.doodle.scheduling.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahana.doodle.scheduling.model.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

	Optional<Calendar> findByUserId(Long userId);

	boolean existsByUserId(Long userId);
}
