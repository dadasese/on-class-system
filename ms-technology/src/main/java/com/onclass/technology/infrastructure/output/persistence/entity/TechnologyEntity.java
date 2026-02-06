package com.onclass.technology.infrastructure.output.persistence.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("technologies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TechnologyEntity {

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;


}
