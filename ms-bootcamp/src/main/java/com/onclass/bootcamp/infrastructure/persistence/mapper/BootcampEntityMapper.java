package com.onclass.bootcamp.infrastructure.persistence.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.infrastructure.entity.BootcampEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class BootcampEntityMapper implements IBootcampEntityMapper {

    @Override
    public BootcampEntity toEntity(Bootcamp b) {
        BootcampEntity bootcamp = new BootcampEntity();
        bootcamp.setId(b.getId());
        bootcamp.setName(b.getName());
        bootcamp.setDescription(b.getDescription());
        bootcamp.setStartDate(b.getStartDate());
        bootcamp.setWeeksDuration(b.getWeeksDuration());
        return bootcamp;
    }

    @Override
    public Bootcamp toDomain(BootcampEntity b) {
        return Bootcamp.builder()
                .id(b.getId())
                .name(b.getName())
                .description(b.getDescription())
                .startDate(b.getStartDate())
                .weeksDuration(b.getWeeksDuration())
                .capacityIds(Collections.emptyList()).build();
    }
}
