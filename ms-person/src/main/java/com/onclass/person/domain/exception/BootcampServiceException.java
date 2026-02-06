package com.onclass.person.domain.exception;

public class BootcampServiceException extends RuntimeException {
    public BootcampServiceException(String message) {
        super("Bootcamp service error: " + message);    }
}
