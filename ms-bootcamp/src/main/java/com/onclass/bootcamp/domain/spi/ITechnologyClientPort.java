package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.CapacityInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyClientPort {
    Mono<Void> deleteById(Long id);
}
