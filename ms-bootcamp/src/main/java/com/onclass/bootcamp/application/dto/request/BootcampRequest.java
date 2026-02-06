package com.onclass.bootcamp.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record BootcampRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name max length is 50")
    String name,

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Description max length is 50")
    String description,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "Weeks duration is required")
    @Min(value = 1, message = "Weeks duration must be at least 1 week")
    Integer weeksDuration,

    @NotNull(message = "CapacityIds is required")
    @Size(min = 1, max = 4, message = "Must have between 1 and 4 capabilities")
    List<Long> capacityIds
) {}
