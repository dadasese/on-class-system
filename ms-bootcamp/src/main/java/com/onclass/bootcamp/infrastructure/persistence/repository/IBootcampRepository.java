package com.onclass.bootcamp.infrastructure.persistence.repository;

import com.onclass.bootcamp.infrastructure.entity.BootcampEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {

    Mono<Boolean> existsByName(String name);

    @Query("SELECT COUNT(*) FROM bootcamps")
    Mono<Long> count();

    @Query("SELECT * FROM bootcamps ORDER BY name ASC LIMIT :size OFFSET :offset")
    Flux<BootcampEntity> findAllByNameAsc(int offset, int size);

    @Query("SELECT * FROM bootcamps ORDER BY name DESC LIMIT :size OFFSET :offset")
    Flux<BootcampEntity> findAllByNameDesc(int offset, int size);

    @Query("""
            SELECT b.* FROM bootcamps b
            LEFT JOIN (
                SELECT bootcamp_id, COUNT(*) AS cap_count
                FROM bootcamp_capacities GROUP BY bootcamp_id
                ) bc ON b.id = bc.bootcamp_id
            ORDER BY COALESCE(bc.cap_count, 0) ASC
            LIMIT :size OFFSET :offset
            """)
    Flux<BootcampEntity> findAllByCapacityCountAsc(int offset, int size);

    @Query("""
            SELECT b.* FROM bootcamps b
            LEFT JOIN (
                SELECT bootcamp_id, COUNT(*) AS cap_count
                FROM bootcamp_capacities GROUP BY bootcamp_id
                ) bc ON b.id = bc.bootcamp_id
            ORDER BY COALESCE(bc.cap_count, 0) DESC
            LIMIT :size OFFSET :offset
            """)
    Flux<BootcampEntity> findAllByCapacityCountDesc(int offset, int size);
}
