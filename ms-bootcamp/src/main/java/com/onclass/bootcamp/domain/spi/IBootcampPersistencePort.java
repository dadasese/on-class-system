package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IBootcampPersistencePort {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Bootcamp> findById(Long id);
    Mono<Boolean> existsByName(String name);
    Flux<Bootcamp> findAllPaginated(int page, int size, String sortBy, String sortDir);
    Mono<Long> count();
    Mono<Void> deleteById(Long id);
    Mono<Void> saveCapacityRelations(Long bootcampId, List<Long> capacityIds);
    Flux<Long> findCapacityIdsByBootcampId(Long bootcampId);
    Mono<Void> deleteCapacityRelationsByBootcampId(Long bootcampId);
    Mono<Long> countBootcampsByCapacityId(Long capacityId);
    Mono<Long> countBootcampsByTechnologyId(Long technologyId);
    Mono<Void> saveCapacityRelation(Long bootcampId, Long capacityId);

}
