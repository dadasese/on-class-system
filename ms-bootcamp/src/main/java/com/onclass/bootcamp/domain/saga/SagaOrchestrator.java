package com.onclass.bootcamp.domain.saga;

import com.onclass.bootcamp.domain.exception.SagaExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SagaOrchestrator {

    public Mono<Void> execute(String sagaName, List<SagaStep<?>> steps, SagaContext context) {
        log.info("Starting saga: {}", sagaName);

        return Flux.fromIterable(steps)
                .index()
                .concatMap(tuple -> {
                    long index = tuple.getT1();
                    SagaStep<?> step = tuple.getT2();

                    log.info("Saga [{}] - Executing step {}/{}: {}",
                            sagaName, index + 1, steps.size(), step.getName());

                    return step.execute(context)
                            .doOnSuccess(result -> {
                                context.markCompleted(step.getName());
                                log.info("Saga [{}] - Step {} completed successfully",
                                        sagaName, step.getName());
                            })
                            .onErrorResume(error -> {
                                log.error("Saga [{}] - Step {} failed: {}",
                                        sagaName, step.getName(), error.getMessage());
                                return compensate(sagaName, steps, context)
                                        .then(Mono.error(new SagaExecutionException(
                                                sagaName, step.getName(), error)));
                            });
                })
                .then()
                .doOnSuccess(v -> log.info("Saga [{}] completed successfully", sagaName));
    }

    private Mono<Void> compensate(String sagaName, List<SagaStep<?>> steps, SagaContext context) {
        log.warn("Saga [{}] - Starting compensation for {} completed steps",
                sagaName, context.getCompletedSteps().size());

        // Get steps that need compensation (in reverse order)
        Map<String, SagaStep<?>> stepMap = steps.stream()
                .collect(Collectors.toMap(SagaStep::getName, s -> s));

        return Flux.fromIterable(context.getCompletedStepsReversed())
                .concatMap(stepName -> {
                    SagaStep<?> step = stepMap.get(stepName);
                    if (step == null) return Mono.empty();

                    log.info("Saga [{}] - Compensating step: {}", sagaName, stepName);

                    return step.compensate(context)
                            .doOnSuccess(v -> {
                                context.markCompensated(stepName);
                                log.info("Saga [{}] - Step {} compensated", sagaName, stepName);
                            })
                            .onErrorResume(error -> {
                                // Log but continue - compensation should be best-effort
                                log.error("Saga [{}] - Compensation failed for {}: {}",
                                        sagaName, stepName, error.getMessage());
                                return Mono.empty();
                            });
                })
                .then()
                .doOnTerminate(() -> log.warn("Saga [{}] - Compensation completed", sagaName));
    }
}