package com.onclass.report.application.mapper;

import com.onclass.report.application.dto.response.BootcampReportResponse;
import com.onclass.report.application.dto.snapshot.CapacitySnapshotDto;
import com.onclass.report.application.dto.snapshot.PersonSnapshotDto;
import com.onclass.report.application.dto.snapshot.TechnologySnapshotDto;
import com.onclass.report.domain.model.BootcampReport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDtoMapper {
    public BootcampReportResponse toResponse(BootcampReport r) {
        return new BootcampReportResponse(
                r.getId(), r.getBootcampId(), r.getName(), r.getDescription(),
                r.getStartDate() != null ? r.getStartDate().toString() : null,
                r.getWeeksDuration(),
                r.getCapacities() != null
                        ? r.getCapacities().stream()
                        .map(c -> new CapacitySnapshotDto(c.id(), c.name(), c.description(),
                                c.technologies().stream()
                                        .map(t -> new TechnologySnapshotDto(t.id(), t.name())).toList(),
                                c.technologyCount()))
                        .toList()
                        : List.of(),
                r.getCapacityCount(), r.getTechnologyCount(),
                r.getPersons() != null
                        ? r.getPersons().stream()
                        .map(p -> new PersonSnapshotDto(p.id(), p.name(), p.email())).toList()
                        : List.of(),
                r.getPersonCount(),
                r.getReportedAt() != null ? r.getReportedAt().toString() : null,
                r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : null);
    }
}
