package com.onclass.capacity.application.mapper;

import com.onclass.capacity.application.dto.request.CapacityRequest;
import com.onclass.capacity.application.dto.response.CapacityResponse;
import com.onclass.capacity.application.dto.response.TechnologyResponse;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.CapacityWithTechnologies;
import com.onclass.capacity.domain.model.TechnologyInfo;

public interface ICapacityMapper {

    Capacity toDomain(CapacityRequest response);
    CapacityResponse toResponse(Capacity capacity);
    CapacityResponse toResponse(CapacityWithTechnologies capacity);
    TechnologyResponse toTechnologyResponse(TechnologyInfo info);
}
