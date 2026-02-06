package com.onclass.report.application.dto.snapshot;

import java.util.List;

public record CapacitySnapshotDto(Long id, String name, String description,
                                  List<TechnologySnapshotDto> technologies, int technologyCount
) {}
