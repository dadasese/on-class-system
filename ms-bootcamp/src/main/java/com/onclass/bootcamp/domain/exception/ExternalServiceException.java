package com.onclass.bootcamp.domain.exception;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final Long resourceId;

    public ExternalServiceException(String message, Long resourceId, Throwable cause) {
        super(message, cause);
        this.resourceId = resourceId;
    }
}