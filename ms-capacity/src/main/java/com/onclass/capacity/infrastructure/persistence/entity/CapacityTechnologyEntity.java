package com.onclass.capacity.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacity_technologies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CapacityTechnologyEntity {

    @Id
    private Long id;

    @Column("capacity_id")
    private Long capacityId;

    @Column("technology_id")
    private Long technologyId;

    public CapacityTechnologyEntity(Long capacityId, Long technologyId) {
        this.capacityId = capacityId;
        this.technologyId = technologyId;
    }
}
