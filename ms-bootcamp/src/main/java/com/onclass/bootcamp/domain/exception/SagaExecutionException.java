package com.onclass.bootcamp.domain.exception;

import lombok.Getter;

@Getter
public class SagaExecutionException extends RuntimeException {

    private final String sagaName;
    private final String failedStep;

    public SagaExecutionException(String sagaName, String failedStep, Throwable cause) {
        super(String.format("Saga '%s' failed at step '%s': %s",
                sagaName, failedStep, cause.getMessage()), cause);
        this.sagaName = sagaName;
        this.failedStep = failedStep;
    }
}
