package com.onclass.bootcamp.domain.api;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBootcampServicePort {
    Mono<BootcampDetail> create(Bootcamp bootcamp);
    Mono<BootcampDetail> findById(Long id);
    Flux<BootcampDetail> findAllPaginated(int page, int size, String sortBy, String sortDir);
    Mono<Bootcamp> update(Long id, Bootcamp bootcamp);
    Mono<Void> delete(Long id);
    Mono<Long> count();
}
