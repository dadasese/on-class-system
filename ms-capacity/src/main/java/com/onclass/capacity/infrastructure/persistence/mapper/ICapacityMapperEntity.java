package com.onclass.capacity.infrastructure.persistence.mapper;

import com.onclass.capacity.application.dto.response.CapacityResponse;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.infrastructure.persistence.entity.CapacityEntity;

public interface ICapacityMapperEntity {
    CapacityEntity toEntity(Capacity domain);
    Capacity toDomain(CapacityEntity entity);

}
