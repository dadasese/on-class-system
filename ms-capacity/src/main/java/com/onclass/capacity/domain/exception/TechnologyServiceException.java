package com.onclass.capacity.domain.exception;

public class TechnologyServiceException extends RuntimeException {
    public TechnologyServiceException(String message) {
        super("Technology service error: " + message);
    }
}
