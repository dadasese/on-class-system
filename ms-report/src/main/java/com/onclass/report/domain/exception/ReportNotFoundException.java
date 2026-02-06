package com.onclass.report.domain.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(Long bootcampId) {
        super("Report for bootcamp " + bootcampId + " not found");    }
}
