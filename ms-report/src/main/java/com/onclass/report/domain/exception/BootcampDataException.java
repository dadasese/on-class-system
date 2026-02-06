package com.onclass.report.domain.exception;

public class BootcampDataException extends RuntimeException {
    public BootcampDataException(String message) {
        super("Failed to collect bootcamp data: " + message);    }
}
