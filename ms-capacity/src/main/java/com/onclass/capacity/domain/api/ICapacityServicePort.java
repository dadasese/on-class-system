package com.onclass.capacity.domain.api;

import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.CapacityWithTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityServicePort {
    Mono<CapacityWithTechnologies> create(Capacity capacity);
    Mono<CapacityWithTechnologies> findById(Long id);
    Flux<CapacityWithTechnologies> findAllPaginated(int page, int size, String sortBy, String sortDir);
    Mono<Capacity> update(Long id, Capacity capacity);
    Mono<Void> delete(Long id);
    Mono<Long> count();
}

