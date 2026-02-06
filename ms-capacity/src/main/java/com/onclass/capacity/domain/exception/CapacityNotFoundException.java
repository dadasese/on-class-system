package com.onclass.capacity.domain.exception;

public class CapacityNotFoundException extends RuntimeException {
    public CapacityNotFoundException(Long id) {
        super("Capacity not found with id: " + id);
    }
}
