package com.onclass.bootcamp.infrastructure.persistence.adapter.external;

import com.onclass.bootcamp.domain.exception.ExternalServiceException;
import com.onclass.bootcamp.domain.spi.ICapacityClientPort;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResilientCapacityClient {

    private final ICapacityClientPort capacityClientPort;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    private static final String CAPACITY_SERVICE = "capacityService";

    public Mono<Void> deleteCapacity(Long capacityId) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CAPACITY_SERVICE);
        Retry retry = retryRegistry.retry(CAPACITY_SERVICE);
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter(CAPACITY_SERVICE);
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(CAPACITY_SERVICE);

        return capacityClientPort.deleteById(capacityId)
                // Apply resilience patterns in correct order
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(BulkheadOperator.of(bulkhead))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RetryOperator.of(retry))
                .doOnSubscribe(s -> log.debug("Attempting to delete capacity {}", capacityId))
                .doOnSuccess(v -> log.info("Successfully deleted capacity {}", capacityId))
                .doOnError(ex -> log.error("Failed to delete capacity {} after retries: {}",
                        capacityId, ex.getMessage()));
    }


    private Mono<Void> deleteCapacityFallback(Long capacityId, Throwable ex) {
        log.warn("Fallback triggered for capacity deletion {}: {}", capacityId, ex.getMessage());

        return Mono.error(new ExternalServiceException(
                "Capacity service unavailable", capacityId, ex));
    }
}