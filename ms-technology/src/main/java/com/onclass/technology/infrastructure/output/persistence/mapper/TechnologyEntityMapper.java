package com.onclass.technology.infrastructure.output.persistence.mapper;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.infrastructure.output.persistence.entity.TechnologyEntity;
import org.springframework.stereotype.Component;

@Component
public class TechnologyEntityMapper implements ITechnologyEntityMapper {

    /**
     * {@inheritDoc}
     * Converts domain model to entity for persistence.
     * Note: createdAt, updatedAt, and active are managed by the database/R2DBC.
     */
    @Override
    public TechnologyEntity toEntity(Technology technology) {
        if (technology == null) {
            return null;
        }

        return TechnologyEntity.builder()
                .id(technology.getId())
                .name(technology.getName())
                .description(technology.getDescription())
                .build();
    }

    /**
     * {@inheritDoc}
     * Converts entity to domain model for business logic.
     * Only essential fields are mapped to the domain model.
     */
    @Override
    public Technology toDomain(TechnologyEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Technology(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
