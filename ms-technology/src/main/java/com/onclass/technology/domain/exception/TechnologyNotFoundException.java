package com.onclass.technology.domain.exception;

public class TechnologyNotFoundException extends RuntimeException {
    public TechnologyNotFoundException(Long id) {
        super(String.valueOf(id));
    }

    public TechnologyNotFoundException(String name) {
        super(name);
    }
}
