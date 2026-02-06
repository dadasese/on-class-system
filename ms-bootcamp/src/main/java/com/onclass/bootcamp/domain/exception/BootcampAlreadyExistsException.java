package com.onclass.bootcamp.domain.exception;

public class BootcampAlreadyExistsException extends RuntimeException {
    public BootcampAlreadyExistsException(String message) {
        super("Bootcamp with name: " + message + "  already exists");
    }
}
