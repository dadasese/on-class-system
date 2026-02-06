package com.onclass.capacity.application.mapper;

import com.onclass.capacity.application.dto.request.CapacityRequest;
import com.onclass.capacity.application.dto.response.CapacityResponse;
import com.onclass.capacity.application.dto.response.TechnologyResponse;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.CapacityWithTechnologies;
import com.onclass.capacity.domain.model.TechnologyInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CapacityMapper implements ICapacityMapper{

    @Override
    public Capacity toDomain(CapacityRequest request) {
        if (request == null){
            return null;
        }

        return new Capacity(
                null,
                request.name(),
                request.description(),
                request.technologyIds()
        );
    }

    @Override
    public CapacityResponse toResponse(Capacity capacity) {
        if (capacity == null){
            return null;
        }

        return new CapacityResponse(
                capacity.getId(),
                capacity.getName(),
                capacity.getDescription(),
                Collections.emptyList(),
                capacity.getTechnologyIds() != null ?
                        capacity.getTechnologyIds().size() : 0
        );
    }

    @Override
    public CapacityResponse toResponse(CapacityWithTechnologies capacity) {
        List<TechnologyResponse> technologies = capacity.technologies().stream()
                .map(this::toTechnologyResponse)
                .toList();

        return new CapacityResponse(
                capacity.id(),
                capacity.name(),
                capacity.description(),
                technologies,
                technologies.size()
        );
    }

    @Override
    public TechnologyResponse toTechnologyResponse(TechnologyInfo info) {
        return new TechnologyResponse(info.id(), info.name());
    }


}
