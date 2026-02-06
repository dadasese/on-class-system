package com.onclass.bootcamp.application.mapper;

import com.onclass.bootcamp.application.dto.request.BootcampRequest;
import com.onclass.bootcamp.application.dto.response.BootcampResponse;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampDetail;

public interface IBootcampDtoMapper {

    Bootcamp toDomain(BootcampRequest r);
    BootcampResponse toResponse(BootcampDetail d);
    BootcampResponse toSimpleResponse(Bootcamp b);
}
