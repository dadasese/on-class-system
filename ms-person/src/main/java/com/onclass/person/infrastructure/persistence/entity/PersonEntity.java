package com.onclass.person.infrastructure.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("persons")
@Setter
@Getter
public class PersonEntity {
    @Id
    private Long id;
    @Column("name") private String name;
    @Column("email") private String email;
    @Column("age") private Integer age;
}
