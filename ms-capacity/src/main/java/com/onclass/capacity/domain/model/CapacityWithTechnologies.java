package com.onclass.capacity.domain.model;

import java.util.List;

public record CapacityWithTechnologies(
        Long id,
        String name,
        String description,
        List<TechnologyInfo> technologies,
        int technologyId
) {}
