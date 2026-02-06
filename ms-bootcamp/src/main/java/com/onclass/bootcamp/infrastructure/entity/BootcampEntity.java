package com.onclass.bootcamp.infrastructure.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("bootcamps")
@Getter
@Setter
public class BootcampEntity {

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("start_date")
    private LocalDate startDate;

    @Column("weeks_duration")
    private Integer weeksDuration;
}
