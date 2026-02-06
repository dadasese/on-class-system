package com.onclass.bootcamp.application.dto.response;

import java.util.List;

public record CapacityResponse(
        Long id,
        String name,
        String description,
        List<TechnologyResponse> technologies,
        int technologyCount
) {
}
