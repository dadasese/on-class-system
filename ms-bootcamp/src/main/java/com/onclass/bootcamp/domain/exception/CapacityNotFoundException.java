package com.onclass.bootcamp.domain.exception;

public class CapacityNotFoundException extends RuntimeException {
    public CapacityNotFoundException(Long id) {
        super("Capacity with id " + id + " not found in capacity service");
    }
}
