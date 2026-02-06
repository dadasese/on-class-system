package com.onclass.person.domain.exception;

public class AlreadyEnrolledException extends RuntimeException {
    public AlreadyEnrolledException(Long personId, Long bootcampId) {
        super("Person " + personId + " is already enrolled in bootcamp " + bootcampId);    }
}
