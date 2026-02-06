package com.onclass.technology.application.mapper;

import com.onclass.technology.application.dto.request.TechnologyRequest;
import com.onclass.technology.application.dto.response.TechnologyResponse;
import com.onclass.technology.domain.model.Technology;

import java.util.List;

public interface ITechnologyMapper {

    Technology toDomain(TechnologyRequest request);

    Technology toDomain(Long id, TechnologyRequest request);

    TechnologyResponse toResponse(Technology technology);

    List<TechnologyResponse> toResponseList(List<Technology> technologyList);
}
