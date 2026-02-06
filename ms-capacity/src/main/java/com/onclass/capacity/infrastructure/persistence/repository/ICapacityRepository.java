package com.onclass.capacity.infrastructure.persistence.repository;

import com.onclass.capacity.infrastructure.persistence.entity.CapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {

    Mono<CapacityEntity> findByName(String name);

    Mono<Boolean> existsByName(String name);

    @Query("SELECT * FROM capacities ORDER BY name ASC LIMIT :size OFFSET :offset")
    Flux<CapacityEntity> findAllByNameAsc(@Param("offset") int offset, @Param("size") int size);

    @Query("SELECT * FROM capacities ORDER BY name DESC LIMIT :size OFFSET :offset")
    Flux<CapacityEntity> findAllByNameDesc(@Param("offset") int offset, @Param("size") int size);

    @Query("""
            SELECT c.* FROM capacities c
            LEFT JOIN (
                SELECT capacity_id, COUNT(*) as tech_count
                FROM capacity_technologies GROUP BY capacity_id
            ) t ON c.id = t.capacity_id
            ORDER BY COALESCE(t.tech_count, 0) ASC, c.name ASC
            LIMIT :size OFFSET :offset
            """)
    Flux<CapacityEntity> findAllByTechnologyCountAsc(@Param("offset") int offset, @Param("size") int size);

    @Query("""
            SELECT c.* FROM capacities c
            LEFT JOIN (
                SELECT capacity_id, COUNT(*) as tech_count
                FROM capacity_technologies GROUP BY capacity_id
            ) t ON c.id = t.capacity_id
            ORDER BY COALESCE(t.tech_count, 0) DESC, c.name ASC
            LIMIT :size OFFSET :offset
            """)
    Flux<CapacityEntity> findAllByTechnologyCountDesc(@Param("offset") int offset, @Param("size") int size);
}
