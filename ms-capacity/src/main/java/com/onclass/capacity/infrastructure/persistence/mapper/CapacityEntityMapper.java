package com.onclass.capacity.infrastructure.persistence.mapper;

import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.infrastructure.persistence.entity.CapacityEntity;
import org.springframework.stereotype.Component;

@Component
public class CapacityEntityMapper implements ICapacityMapperEntity{

    @Override
    public CapacityEntity toEntity(Capacity domain) {
        CapacityEntity entity = new CapacityEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }

    @Override
    public Capacity toDomain(CapacityEntity entity) {
        return Capacity.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}
