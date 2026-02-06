package com.onclass.bootcamp.infrastructure.persistence.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.infrastructure.entity.BootcampEntity;

public interface IBootcampEntityMapper {

    BootcampEntity toEntity(Bootcamp b);
    Bootcamp toDomain(BootcampEntity b);
}
