package com.onclass.person.domain.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(Long id) {
        super("Person with id " + id + " not found");
    }
}