package com.onclass.technology.domain.exception;

public class TechnologyAlreadyExistsException extends RuntimeException{

    public TechnologyAlreadyExistsException(String name){
        super(name);
    }
}
