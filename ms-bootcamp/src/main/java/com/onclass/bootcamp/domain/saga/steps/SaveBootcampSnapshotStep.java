package com.onclass.bootcamp.domain.saga.steps;

import com.onclass.bootcamp.domain.exception.BootcampNotFoundException;
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
public class SaveBootcampSnapshotStep implements SagaStep<Bootcamp> {

    private final IBootcampPersistencePort persistencePort;

    public static final String STEP_NAME = "SAVE_BOOTCAMP_SNAPSHOT";
    public static final String BOOTCAMP_SNAPSHOT_KEY = "bootcamp_snapshot";
    public static final String CAPACITY_IDS_KEY = "capacity_ids";
    public static final String BOOTCAMP_ID_KEY = "bootcamp_id";

    @Override
    public String getName() {
        return STEP_NAME;
    }

    @Override
    public Mono<Bootcamp> execute(SagaContext context) {
        Long bootcampId = context.get(BOOTCAMP_ID_KEY, Long.class);

        return persistencePort.findById(bootcampId)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(bootcampId)))
                .flatMap(bootcamp ->
                        persistencePort.findCapacityIdsByBootcampId(bootcampId)
                                .collectList()
                                .map(capacityIds -> {
                                    // Save snapshot for potential rollback
                                    context.put(BOOTCAMP_SNAPSHOT_KEY, bootcamp);
                                    context.put(CAPACITY_IDS_KEY, capacityIds);
                                    log.debug("Snapshot saved: bootcamp={}, capacityIds={}",
                                            bootcamp.getId(), capacityIds);
                                    return bootcamp;
                                })
                );
    }

    @Override
    public Mono<Void> compensate(SagaContext context) {
        // Nothing to compensate - this was just reading data
        return Mono.empty();
    }
}
