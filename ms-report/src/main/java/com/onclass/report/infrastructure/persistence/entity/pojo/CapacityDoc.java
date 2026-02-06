package com.onclass.report.infrastructure.persistence.entity.pojo;

import java.util.List;

public record CapacityDoc(Long id, String name, String description,
                          List<TechnologyDoc> technologies, int technologyCount) {}
