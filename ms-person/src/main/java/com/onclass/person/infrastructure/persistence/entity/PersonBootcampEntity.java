package com.onclass.person.infrastructure.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("person_bootcamps")
@NoArgsConstructor
@Setter
@Getter
public class PersonBootcampEntity {
    @Id
    private Long id;
    @Column("person_id") private Long personId;
    @Column("bootcamp_id") private Long bootcampId;

    public PersonBootcampEntity(Long personId, Long bootcampId) {
        this.personId = personId;
        this.bootcampId = bootcampId;
    }
}
