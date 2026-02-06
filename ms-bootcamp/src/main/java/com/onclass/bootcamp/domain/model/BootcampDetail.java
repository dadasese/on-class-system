package com.onclass.bootcamp.domain.model;

import java.util.List;

public record BootcampDetail(
        Long id,
        String name,
        String description,
        String startDate,
        Integer weeksDuration,
        List<CapacityInfo> capacities,
        int capacityCount
) {
}
