package com.onclass.bootcamp.domain.saga.steps;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.saga.SagaContext;
import com.onclass.bootcamp.domain.saga.SagaStep;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteBootcampStep implements SagaStep<Void> {

    private final IBootcampPersistencePort persistencePort;

    public static final String STEP_NAME = "DELETE_BOOTCAMP";

    @Override
    public String getName() {
        return STEP_NAME;
    }

    @Override
    public Mono<Void> execute(SagaContext context) {
        Long bootcampId = context.get(SaveBootcampSnapshotStep.BOOTCAMP_ID_KEY, Long.class);
        return persistencePort.deleteById(bootcampId);
    }

    @Override
    public Mono<Void> compensate(SagaContext context) {
        Bootcamp snapshot = context.get(SaveBootcampSnapshotStep.BOOTCAMP_SNAPSHOT_KEY, Bootcamp.class);

        if (snapshot == null) {
            log.warn("No bootcamp snapshot found for compensation");
            return Mono.empty();
        }

        log.info("Restoring bootcamp from snapshot: {}", snapshot.getId());
        return persistencePort.save(snapshot).then();
    }
}