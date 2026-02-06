package com.onclass.capacity.domain.exception;

public class CapacityAlreadyExistsException extends RuntimeException {
    public CapacityAlreadyExistsException(String message) {
        super("Capacity already exists: " + message);
    }
}
