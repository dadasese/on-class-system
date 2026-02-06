package com.onclass.bootcamp.application.dto.response;

import java.util.List;

public record BootcampResponse(
        Long id,
        String name,
        String description,
        String startDate,
        Integer weeksDuration,
        List<CapacityResponse> capacities,
        int capacityCount
) {
}
