package com.onclass.report.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
public class BootcampReport {
    private String id;
    private Long bootcampId;
    private String name;
    private String description;
    private LocalDate startDate;
    private Integer weeksDuration;

    private List<CapacitySnapshot> capacities;
    private int capacityCount;
    private int technologyCount;

    private List<PersonSnapshot> persons;
    private int personCount;

    private LocalDateTime reportedAt;
    private LocalDateTime updatedAt;
}
