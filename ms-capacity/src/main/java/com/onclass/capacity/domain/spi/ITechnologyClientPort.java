package com.onclass.capacity.domain.spi;

import com.onclass.capacity.domain.model.TechnologyInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyClientPort
{
    Mono<Boolean> existsById(Long id);
    Flux<TechnologyInfo> findByIds(List<Long> ids);
    Mono<TechnologyInfo> findById(Long id);
    Mono<Void> deleteById(Long id);
}
