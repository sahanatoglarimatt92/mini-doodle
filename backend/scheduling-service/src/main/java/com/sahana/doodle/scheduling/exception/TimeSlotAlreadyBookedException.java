package com.sahana.doodle.scheduling.exception;

public class TimeSlotAlreadyBookedException extends RuntimeException {

    public TimeSlotAlreadyBookedException(String message) {
        super(message);
    }
}