package com.onclass.person.domain.exception;

public class BootcampOverlapException extends RuntimeException {
    public BootcampOverlapException(String bootcampA, String bootcampB) {
        super("Bootcamp '" + bootcampA + "' overlaps with '" + bootcampB + "'");    }
}
