package com.onclass.capacity.infrastructure.persistence.repository;

import com.onclass.capacity.infrastructure.persistence.entity.CapacityTechnologyEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityTechnologyRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long> {

    Flux<CapacityTechnologyEntity> findByCapacityId(Long capacityId);

    @Query("SELECT technology_id FROM capacity_technologies WHERE capacity_id = :capacityId")
    Flux<Long> findTechnologyIdsByCapacityId(Long capacityId);

    @Query("SELECT COUNT(*) FROM capacity_technologies WHERE capacity_id = :capacityId")
    Mono<Integer> countByCapacityId(Long capacityID);

    @Modifying
    @Query("DELETE FROM capacity_technologies WHERE capacity_id = :capacityId")
    Mono<Void> deleteByCapacityId(Long capacityId);

    @Query("SELECT COUNT(*) FROM capacity_technologies WHERE technology_id = :technology_id")
    Mono<Integer> countByTechnologyId(Long technology_id);
}
