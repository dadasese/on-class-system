package com.onclass.person.infrastructure.persistence.repository;

import com.onclass.person.infrastructure.persistence.entity.PersonEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPersonRepository extends ReactiveCrudRepository<PersonEntity, Long> {
    Mono<PersonEntity> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT COUNT(*) FROM persons")
    Mono<Long> countActive();

    @Query("SELECT * FROM persons ORDER BY name ASC LIMIT :size OFFSET :offset")
    Flux<PersonEntity> findAllByNameAsc(int offset, int size);

    @Query("SELECT * FROM persons ORDER BY name DESC LIMIT :size OFFSET :offset")
    Flux<PersonEntity> findAllByNameDesc(int offset, int size);

    @Query("SELECT * FROM persons ORDER BY age ASC LIMIT :size OFFSET :offset")
    Flux<PersonEntity> findAllByAgeAsc(int offset, int size);

    @Query("SELECT * FROM persons ORDER BY age DESC LIMIT :size OFFSET :offset")
    Flux<PersonEntity> findAllByAgeDesc(int offset, int size);
}
