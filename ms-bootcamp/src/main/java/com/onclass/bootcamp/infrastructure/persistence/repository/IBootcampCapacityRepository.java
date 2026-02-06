package com.onclass.bootcamp.infrastructure.persistence.repository;

import com.onclass.bootcamp.infrastructure.entity.BootcampCapacityEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBootcampCapacityRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long> {

    @Query("SELECT capacity_id FROM bootcamp_capacities WHERE bootcamp_id = :bootcampId")
    Flux<Long> findCapacityIdsByBootcampId(Long bootcampId);

    @Modifying
    @Query("DELETE FROM bootcamp_capacities WHERE bootcamp_id = :bootcampId")
    Mono<Void> deleteByBootcampId(Long bootcampId);

    @Query("SELECT COUNT(*) FROM bootcamp_capacities WHERE capacity_id = :capacityId")
    Mono<Long> countByCapacityId(Long capacityId);

    @Query("SELECT COUNT(*) FROM bootcamp_capacities WHERE technologyId = :technologyId")
    Mono<Long> countByTechnologyId(Long technologyId);
}
