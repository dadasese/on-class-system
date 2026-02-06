package com.onclass.report.infrastructure.persistence.entity;

import com.onclass.report.infrastructure.persistence.entity.pojo.CapacityDoc;
import com.onclass.report.infrastructure.persistence.entity.pojo.PersonDoc;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "bootcamp_reports")
@Setter
@Getter
public class BootcampReportDocument {
    @Id
    private String id;

    @Indexed(unique = true)
    @Field("bootcamp_id")
    private Long bootcampId;

    private String name;
    private String description;

    @Field("start_date")
    private LocalDate startDate;

    @Field("weeks_duration")
    private Integer weeksDuration;

    // Embedded arrays (denormalized)
    private List<CapacityDoc> capacities;

    @Indexed
    @Field("capacity_count")
    private int capacityCount;

    @Field("technology_count")
    private int technologyCount;

    private List<PersonDoc> persons;

    @Indexed
    @Field("person_count")
    private int personCount;

    @Field("reported_at")
    private LocalDateTime reportedAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
