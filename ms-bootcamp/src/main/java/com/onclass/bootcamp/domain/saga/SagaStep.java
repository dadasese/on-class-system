package com.onclass.bootcamp.domain.saga;

import reactor.core.publisher.Mono;

public interface SagaStep<T> {

    String getName();

    Mono<T> execute(SagaContext context);

    Mono<Void> compensate(SagaContext context);
}
