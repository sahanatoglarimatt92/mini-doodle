package com.sahana.doodle.scheduling.exception;

public class ParticipantNotFoundInMeetingException extends RuntimeException {

    public ParticipantNotFoundInMeetingException(String message) {
        super(message);
    }
}