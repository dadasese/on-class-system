package com.onclass.person.infrastructure.persistence.repository;

import com.onclass.person.domain.model.Person;
import com.onclass.person.infrastructure.persistence.entity.PersonBootcampEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPersonBootcampRepository extends ReactiveCrudRepository<PersonBootcampEntity, Long> {
    @Query("SELECT bootcamp_id FROM person_bootcamps WHERE person_id = :personId")
    Flux<Long> findBootcampIdsByPersonId(Long personId);

    @Query("SELECT COUNT(*) FROM person_bootcamps WHERE person_id = :personId")
    Mono<Integer> countByPersonId(Long personId);

    @Modifying
    @Query("DELETE FROM person_bootcamps WHERE person_id = :personId AND bootcamp_id = :bootcampId")
    Mono<Void> deleteByPersonIdAndBootcampId(Long personId, Long bootcampId);

    Mono<Boolean> existsByPersonIdAndBootcampId(Long personId, Long bootcampId);
    
}
