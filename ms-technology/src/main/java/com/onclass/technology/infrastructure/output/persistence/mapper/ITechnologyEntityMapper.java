package com.onclass.technology.infrastructure.output.persistence.mapper;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.infrastructure.output.persistence.entity.TechnologyEntity;

public interface ITechnologyEntityMapper {

    TechnologyEntity toEntity(Technology technology);

    Technology toDomain(TechnologyEntity entity);
}
