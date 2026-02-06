package com.onclass.technology.infrastructure.output.persistence.repository;

import com.onclass.technology.infrastructure.output.persistence.entity.TechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {
    
    @Query("SELECT * FROM technologies WHERE name = :name ")
    Mono<TechnologyEntity> findByName(String name);

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM technologies WHERE name = :name ")
    Mono<Boolean> existByName(String name);

    @Query("SELECT * FROM technologies ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<TechnologyEntity> findAllPaginatedAsc(int limit, long offset);

    @Query("SELECT * FROM technologies ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<TechnologyEntity> findAllPaginatedDesc(int limit, long offset);

    @Query("SELECT * FROM technologies WHERE id = :id")
    Mono<TechnologyEntity> findById(Long id);

    @Query("SELECT COUNT(*) FROM technologies")
    Mono<Long> count();

    @Query("SELECT COUNT(*) FROM technologies WHERE name = :name")
    Mono<Long> countByName(String name);

    @Query("SELECT * FROM technologies WHERE id IN (:ids) ")
    Flux<TechnologyEntity> findAllByIdIn(List<Long> ids);
}
