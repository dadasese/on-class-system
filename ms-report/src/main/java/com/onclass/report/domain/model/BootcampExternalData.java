package com.onclass.report.domain.model;

import java.util.List;

public record BootcampExternalData(
        Long id,
        String name,
        String description,
        String startDate,
        Integer weeksDuration,
        List<CapacityExternalData> capacities,
        int capacityCount
) {
}
