package com.sahana.doodle.scheduling.exception;

public class OverlappingTimeSlotException extends RuntimeException {

    public OverlappingTimeSlotException(String message) {
        super(message);
    }
}
