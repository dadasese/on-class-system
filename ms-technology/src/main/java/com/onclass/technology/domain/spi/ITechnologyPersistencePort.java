package com.onclass.technology.domain.spi;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyPersistencePort {
    Mono<Technology> save(Technology technology);
    Mono<Technology> update(Technology technology);
    Mono<Technology> findById(Long id);
    Mono<Technology> findByName(String name);
    Mono<Boolean> existByName(String name);
    Flux<Technology> findAll(int page, int size, String sortBy, String sortDir);
    Mono<Long> count();
    Mono<Void> deleteById(Long id);
    Flux<Technology> findAllByIds(List<Long> ids);
}
