package com.onclass.person.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EnrollRequest(
        @NotNull(message = "bootcampIds is required")
        @Size(min = 1, max = 5, message = "Must provide 1-5 bootcamp IDs")
        List<Long> bootcampIds
) { }
