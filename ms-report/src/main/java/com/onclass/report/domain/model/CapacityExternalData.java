package com.onclass.report.domain.model;

import java.util.List;

public record CapacityExternalData(
        Long id,
        String name,
        String description,
        List<TechnologyExternalData> technologies,
        int technologyCount
) {
}
