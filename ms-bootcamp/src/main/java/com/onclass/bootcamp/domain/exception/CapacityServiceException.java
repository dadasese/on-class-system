package com.onclass.bootcamp.domain.exception;

public class CapacityServiceException extends RuntimeException {
    public CapacityServiceException(String message) {
        super("Capacity service error: " + message);
    }
}
