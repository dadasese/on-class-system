package com.onclass.person.domain.exception;

public class BootcampNotFoundException extends RuntimeException {
    public BootcampNotFoundException(Long id) {
        super("Bootcamp with id " + id + " not found in bootcamp service");    }
}
