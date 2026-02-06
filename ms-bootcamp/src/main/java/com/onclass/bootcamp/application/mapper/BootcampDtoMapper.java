package com.onclass.bootcamp.application.mapper;

import com.onclass.bootcamp.application.dto.request.BootcampRequest;
import com.onclass.bootcamp.application.dto.response.BootcampResponse;
import com.onclass.bootcamp.application.dto.response.CapacityResponse;
import com.onclass.bootcamp.application.dto.response.TechnologyResponse;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampDetail;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BootcampDtoMapper implements IBootcampDtoMapper{
    @Override
    public Bootcamp toDomain(BootcampRequest r) {
        return Bootcamp.builder()
                .name(r.name())
                .description(r.description())
                .startDate(r.startDate())
                .weeksDuration(r.weeksDuration())
                .capacityIds(r.capacityIds()).build();
    }

    @Override
    public BootcampResponse toResponse(BootcampDetail d) {
        List<CapacityResponse> capacityResponses = d.capacities().stream()
                .map(c -> new CapacityResponse(
                        c.id(), c.name(), c.description(), c.technologies().stream()
                        .map(t -> new TechnologyResponse(t.id(), t.name()))
                        .toList(),
                        c.technologyCount())).toList();
        return new BootcampResponse(
                d.id(), d.name(), d.description(), d.startDate(), d.weeksDuration(),
                capacityResponses, d.capacityCount()
        );
    }

    @Override
    public BootcampResponse toSimpleResponse(Bootcamp b) {
        return new BootcampResponse(
                b.getId(), b.getName(), b.getDescription(),
                b.getStartDate() != null ? b.getStartDate().toString() : null,
                b.getWeeksDuration(), List.of(),
                b.getCapacityIds() != null ? b.getCapacityIds().size() : 0
        );
    }
}
