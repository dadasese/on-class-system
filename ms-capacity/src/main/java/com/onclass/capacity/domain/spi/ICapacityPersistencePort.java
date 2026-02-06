package com.onclass.capacity.domain.spi;

import com.onclass.capacity.domain.model.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityPersistencePort {
    Mono<Capacity> save(Capacity capacity);
    Mono<Capacity> findById(Long id);
    Mono<Capacity> findByName(String name);
    Mono<Boolean> existsByName(String name);
    Flux<Capacity> findAllPaginated(int page, int size, String sortBy, String sortDir);
    Mono<Long> count();
    Mono<Void> deleteById(Long id);
    Mono<Void> saveTechnologyRelation(Long capacityId, List<Long> technologyIds);
    Mono<Void> deleteTechnologyRelationsByCapacityId(Long capacityId);
    Flux<Long> findTechnologyByCapacityId(Long capacityId);
    Mono<Integer> countTechnologyByCapacityId(Long capacityId);
}
