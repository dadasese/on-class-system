package com.onclass.technology.domain.api;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyServicePort {
    Mono<Technology> createTechnology(Technology technology);
    Mono<Technology> getTechnologyById(Long id);
    Mono<Technology> getTechnologyByName(String name);
    Flux<Technology> getAllTechnologies(int page, int size, String sortBy, String sortDirection);
    Mono<Long> countTechnologies();
    Mono<Technology> updateTechnology(Long id, Technology technology);
    Mono<Void> deleteTechnology(Long id);
    Flux<Technology> getTechnologiesByIds(List<Long> ids);

}
