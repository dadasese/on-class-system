package com.onclass.report.application.dto.response;

import com.onclass.report.application.dto.snapshot.CapacitySnapshotDto;
import com.onclass.report.application.dto.snapshot.PersonSnapshotDto;

import java.util.List;

public record BootcampReportResponse(
        String id,
        Long bootcampId,
        String name,
        String description,
        String startDate,
        Integer weeksDuration,
        List<CapacitySnapshotDto> capacities,
        int capacityCount,
        int technologyCount,
        List<PersonSnapshotDto> persons,
        int personCount,
        String reportedAt,
        String updatedAt
) {
}
