package com.onclass.capacity.domain.exception;

public class TechnologyNotFoundException extends RuntimeException {
    public TechnologyNotFoundException(Long id) {
        super("Technology not found with id: " + id);
    }
}
