package com.onclass.capacity.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CapacityRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name max length is 50")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 90, message = "Description max length is 90")
        String description,

        @NotNull(message = "TechnologyIds is required")
        @Size(min = 3, max = 20, message = "Must have between 3 and 20 technologies")
        List<Long> technologyIds
) {}
