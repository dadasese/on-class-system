package com.onclass.bootcamp.domain.saga.steps;

import com.onclass.bootcamp.domain.saga.SagaContext;
import com.onclass.bootcamp.domain.saga.SagaStep;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteCapacityRelationsStep implements SagaStep<Void> {

    private final IBootcampPersistencePort persistencePort;

    public static final String STEP_NAME = "DELETE_CAPACITY_RELATIONS";

    @Override
    public String getName() {
        return STEP_NAME;
    }

    @Override
    public Mono<Void> execute(SagaContext context) {
        Long bootcampId = context.get(SaveBootcampSnapshotStep.BOOTCAMP_ID_KEY, Long.class);
        return persistencePort.deleteCapacityRelationsByBootcampId(bootcampId);
    }

    @Override
    public Mono<Void> compensate(SagaContext context) {
        Long bootcampId = context.get(SaveBootcampSnapshotStep.BOOTCAMP_ID_KEY, Long.class);

        @SuppressWarnings("unchecked")
        List<Long> capacityIds = context.get(SaveBootcampSnapshotStep.CAPACITY_IDS_KEY, List.class);

        if (capacityIds == null || capacityIds.isEmpty()) {
            return Mono.empty();
        }

        log.info("Restoring capacity relations for bootcamp {}: {}", bootcampId, capacityIds);

        return persistencePort.saveCapacityRelations(bootcampId, capacityIds)
                .then();
    }
}
