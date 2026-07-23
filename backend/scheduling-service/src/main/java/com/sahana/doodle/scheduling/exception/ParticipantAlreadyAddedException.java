package com.sahana.doodle.scheduling.exception;

public class ParticipantAlreadyAddedException extends RuntimeException {

    public ParticipantAlreadyAddedException(String message) {
        super(message);
    }
}