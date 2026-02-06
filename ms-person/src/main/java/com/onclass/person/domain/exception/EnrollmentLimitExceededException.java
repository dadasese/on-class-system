package com.onclass.person.domain.exception;

public class EnrollmentLimitExceededException extends RuntimeException {
    public EnrollmentLimitExceededException(int current, int requested) {
        super("Cannot enroll in " + requested + " bootcamp(s). "
                + "Already enrolled in " + current + ". Maximum is 5.");
    }
}
