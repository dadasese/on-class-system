package com.onclass.person.application.dto;

public record BootcampInfoResponse (
        Long id, String name, String description,
        String startDate, Integer weeksDuration
){}
