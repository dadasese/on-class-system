package com.onclass.technology.application.mapper;


import com.onclass.technology.application.dto.request.TechnologyRequest;
import com.onclass.technology.application.dto.response.TechnologyResponse;
import com.onclass.technology.domain.model.Technology;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TechnologyMapper implements ITechnologyMapper {

    @Override
    public Technology toDomain(TechnologyRequest request) {
        if (request == null){
            return null;
        }

        return new Technology(
                null,
                request.getName(),
                request.getDescription()
        );
    }

    @Override
    public Technology toDomain(Long id, TechnologyRequest request) {
        if (request == null){
            return null;
        }

        return new Technology(
                id,
                request.getName(),
                request.getDescription()
        );
    }

    @Override
    public TechnologyResponse toResponse(Technology technology) {
        if (technology == null){
            return null;
        }

        return TechnologyResponse.builder()
                .id(technology.getId())
                .name(technology.getName())
                .description(technology.getDescription()).build();
    }

    @Override
    public List<TechnologyResponse> toResponseList(List<Technology> technologyList) {

        if (technologyList == null || technologyList.isEmpty()){
            return Collections.emptyList();
        }

        return technologyList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
