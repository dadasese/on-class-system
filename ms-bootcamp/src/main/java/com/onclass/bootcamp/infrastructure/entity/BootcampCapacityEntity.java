package com.onclass.bootcamp.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("bootcamp_capacities")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BootcampCapacityEntity {
    @Id
    private Long id;

    @Column("bootcamp_id")
    private Long bootcampId;

    @Column("capacity_id")
    private Long capacityId;

    public BootcampCapacityEntity(Long bootcampId, Long capacityId) {
        this.bootcampId = bootcampId;
        this.capacityId = capacityId;
    }
}
