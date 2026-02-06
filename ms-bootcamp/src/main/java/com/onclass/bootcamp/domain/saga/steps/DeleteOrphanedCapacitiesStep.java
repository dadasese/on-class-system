package com.onclass.bootcamp.domain.saga.steps;

import com.onclass.bootcamp.domain.saga.SagaContext;
import com.onclass.bootcamp.domain.saga.SagaStep;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.infrastructure.persistence.adapter.external.ResilientCapacityClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteOrphanedCapacitiesStep implements SagaStep<Void> {

    private final IBootcampPersistencePort persistencePort;
    private final ResilientCapacityClient resilientCapacityClient;

    public static final String STEP_NAME = "DELETE_ORPHANED_CAPACITIES";
    public static final String DELETED_CAPACITY_IDS_KEY = "deleted_capacity_ids";

    @Override
    public String getName() {
        return STEP_NAME;
    }

    @Override
    public Mono<Void> execute(SagaContext context) {
        @SuppressWarnings("unchecked")
        List<Long> capacityIds = context.get(SaveBootcampSnapshotStep.CAPACITY_IDS_KEY, List.class);

        if (capacityIds == null || capacityIds.isEmpty()) {
            return Mono.empty();
        }

        List<Long> deletedCapacityIds = new CopyOnWriteArrayList<>();
        context.put(DELETED_CAPACITY_IDS_KEY, deletedCapacityIds);

        return Flux.fromIterable(capacityIds)
                .flatMap(capacityId ->
                        persistencePort.countBootcampsByCapacityId(capacityId)
                                .flatMap(count -> {
                                    if (count == 0) {
                                        log.info("Capacity {} is orphaned, requesting deletion", capacityId);
                                        return resilientCapacityClient.deleteCapacity(capacityId)
                                                .doOnSuccess(v -> deletedCapacityIds.add(capacityId));
                                    }
                                    log.debug("Capacity {} still referenced by {} bootcamp(s)",
                                            capacityId, count);
                                    return Mono.empty();
                                })
                )
                .then();
    }

    @Override
    public Mono<Void> compensate(SagaContext context) {
        // External service deletion - we can try to recreate if we have the data
        // In practice, you might need to call a "restore" endpoint or log for manual intervention

        @SuppressWarnings("unchecked")
        List<Long> deletedIds = context.get(DELETED_CAPACITY_IDS_KEY, List.class);

        if (deletedIds == null || deletedIds.isEmpty()) {
            return Mono.empty();
        }

        log.warn("Cannot automatically restore deleted capacities: {}. Manual intervention may be required.",
                deletedIds);


        return Mono.empty();
    }
}
