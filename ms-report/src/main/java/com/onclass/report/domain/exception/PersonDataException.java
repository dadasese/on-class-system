package com.onclass.report.domain.exception;

public class PersonDataException extends RuntimeException {
    public PersonDataException(String message) {
        super("Failed to collect persona data: " + message);
    }
}
