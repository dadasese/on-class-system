package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.CapacityInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityClientPort {
    Mono<Boolean> existsById(Long id);
    Mono<CapacityInfo> findById(Long id);
    Flux<CapacityInfo> findByIds(List<Long> ids);
    Mono<Void> deleteById(Long id);
}
