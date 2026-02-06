package com.onclass.bootcamp.domain.model;

import java.util.List;

public record CapacityInfo(
        Long id,
        String name,
        String description,
        List<TechnologyInfo> technologies,
        int technologyCount
) {
}
