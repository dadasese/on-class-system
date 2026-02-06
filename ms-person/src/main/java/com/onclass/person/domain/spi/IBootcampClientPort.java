package com.onclass.person.domain.spi;

import com.onclass.person.domain.model.BootcampInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IBootcampClientPort {
    Mono<BootcampInfo> findById(Long id);
    Flux<BootcampInfo> findByIds(List<Long> ids);
    Mono<Boolean> existsById(Long id);
}
