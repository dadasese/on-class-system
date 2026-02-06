package com.onclass.report.domain.model;

import java.util.List;

public record CapacitySnapshot(
        Long id,
        String name,
        String description,
        List<TechnologySnapshot> technologies,
        int technologyCount
) {
}
