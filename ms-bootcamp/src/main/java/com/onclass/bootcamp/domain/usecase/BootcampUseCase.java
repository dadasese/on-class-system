package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.api.IBootcampServicePort;
import com.onclass.bootcamp.domain.exception.BootcampAlreadyExistsException;
import com.onclass.bootcamp.domain.exception.BootcampNotFoundException;
import com.onclass.bootcamp.domain.exception.CapacityNotFoundException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampDetail;
import com.onclass.bootcamp.domain.model.CapacityInfo;
import com.onclass.bootcamp.domain.saga.SagaContext;
import com.onclass.bootcamp.domain.saga.SagaOrchestrator;
import com.onclass.bootcamp.domain.saga.SagaStep;
import com.onclass.bootcamp.domain.saga.steps.DeleteBootcampStep;
import com.onclass.bootcamp.domain.saga.steps.DeleteCapacityRelationsStep;
import com.onclass.bootcamp.domain.saga.steps.DeleteOrphanedCapacitiesStep;
import com.onclass.bootcamp.domain.saga.steps.SaveBootcampSnapshotStep;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.ICapacityClientPort;
import com.onclass.bootcamp.domain.spi.ITechnologyClientPort;
import com.onclass.bootcamp.infrastructure.client.ReportEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BootcampUseCase implements IBootcampServicePort {

    private static final Logger log = LoggerFactory.getLogger(BootcampUseCase.class);
    private final IBootcampPersistencePort persistencePort;
    private final ICapacityClientPort capacityClientPort;
    private final ReportEventPublisher reportEventPublisher;

    private final SagaOrchestrator sagaOrchestrator;
    private final SaveBootcampSnapshotStep snapshotStep;
    private final DeleteCapacityRelationsStep deleteRelationsStep;
    private final DeleteBootcampStep deleteBootcampStep;
    private final DeleteOrphanedCapacitiesStep deleteCapacitiesStep;

    private static final String DELETE_BOOTCAMP_SAGA = "DELETE_BOOTCAMP";



    @Override
    public Mono<BootcampDetail> create(Bootcamp bootcamp) {
        bootcamp.validate();

        return persistencePort.existsByName(bootcamp.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.<Void> error(
                                new BootcampAlreadyExistsException(bootcamp.getName()));
                    } return validateCapacitiesExists(bootcamp.getCapacityIds());
                })
                .then(persistencePort.save(bootcamp))
                .flatMap(saved ->
                        persistencePort.saveCapacityRelations(
                                saved.getId(), bootcamp.getCapacityIds()
                        ).thenReturn(saved))
                .flatMap(this::toBootcampDetail)
                .doOnSuccess(b -> reportEventPublisher.publishBootcampCreated(b.id()));
    }

    @Override
    public Mono<BootcampDetail> findById(Long id) {
        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(id)))
                .flatMap(this::toBootcampDetail);
    }

    @Override
    public Flux<BootcampDetail> findAllPaginated(int page, int size, String sortBy, String sortDir) {
        return persistencePort.findAllPaginated(page, size, sortBy, sortDir)
                .concatMap(this::toBootcampDetail);
    }

    @Override
    public Mono<Bootcamp> update(Long id, Bootcamp bootcamp) {
        bootcamp.validate();

        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(id)))
                .flatMap(existing -> {
                    if (!existing.getName().equalsIgnoreCase(bootcamp.getName())) {

                        return persistencePort.existsByName(bootcamp.getName())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new BootcampAlreadyExistsException(bootcamp.getName()));
                                    }
                                    return Mono.just(existing);
                                });
                    }
                    return Mono.just(existing);
                })
                .flatMap(existing ->
                        validateCapacitiesExists(bootcamp.getCapacityIds())
                                .thenReturn(existing))
                .flatMap(existing -> {
                    Bootcamp updated = Bootcamp.builder()
                            .id(id)
                            .name(bootcamp.getName())
                            .description(bootcamp.getDescription())
                            .startDate(bootcamp.getStartDate())
                            .weeksDuration(bootcamp.getWeeksDuration())
                            .capacityIds(bootcamp.getCapacityIds()).build();
                    return persistencePort.save(updated);
                })
                .flatMap(saved ->
                        persistencePort.saveCapacityRelations(saved.getId(), bootcamp.getCapacityIds())
                                .thenReturn(saved));
    }

    @Override
    public Mono<Void> delete(Long id) {
        SagaContext context = new SagaContext();
        context.put(SaveBootcampSnapshotStep.BOOTCAMP_ID_KEY, id);

        List<SagaStep<?>> steps = List.of(
                snapshotStep,
                deleteRelationsStep,
                deleteBootcampStep,
                deleteCapacitiesStep
        );

        return sagaOrchestrator.execute(DELETE_BOOTCAMP_SAGA, steps, context)
                .doOnSuccess(v -> log.info("Bootcamp {} deleted successfully via saga", id))
                .doOnError(ex -> log.error("Saga failed for bootcamp {}: {}", id, ex.getMessage()));
    }


    @Override
    public Mono<Long> count() {
        return persistencePort.count();
    }

    private Mono<Void> validateCapacitiesExists(List<Long> ids){
        return Flux.fromIterable(ids)
                .flatMap(id -> capacityClientPort.existsById(id)
                        .flatMap(exists -> {
                            if (!exists) {
                                return Mono.error((new CapacityNotFoundException(id)));
                            }
                            return Mono.empty();
                        }))
                .then();
    }

    private Mono<BootcampDetail> toBootcampDetail(Bootcamp bootcamp){
        return persistencePort.findCapacityIdsByBootcampId(bootcamp.getId())
                .collectList()
                .flatMap(capacityIds -> {
                    if (capacityIds.isEmpty()) {
                        return Mono.just(buildDetail(bootcamp, List.of()));
                    }
                    return capacityClientPort.findByIds(capacityIds)
                            .collectList()
                            .map(capacityInfos -> buildDetail(bootcamp, capacityInfos));
                });
    }

    private BootcampDetail buildDetail(Bootcamp b, List<CapacityInfo> capacityInfos){
        return new BootcampDetail(
                b.getId(),
                b.getName(),
                b.getDescription(),
                b.getStartDate() != null ?
                        b.getStartDate().toString() : null,
                b.getWeeksDuration(),
                capacityInfos,
                capacityInfos.size()
        );
    }

    private Mono<Void> deleteOrphanedCapacities(List<Long> capacityIds) {
        return Flux.fromIterable(capacityIds)
                .flatMap(capacityId ->
                        persistencePort.countBootcampsByCapacityId(capacityId)
                                .flatMap(count ->
                                {
                                    if (count == 0) {
                                        log.info("Capacity id={} is orphaned requesting deletion", capacityId);
                                        return capacityClientPort.deleteById(capacityId)
                                                .onErrorResume(ex -> {
                                                    log.error("Failed to delete orphaned capacity {}: {}", capacityId, ex.getMessage());
                                                    return Mono.empty();
                                                });
                                    }
                                    log.debug("Capacity Id = {} still referenced by {} bootcamp(s)", capacityId, count);
                                    return Mono.empty();
                                })
                ).then();
    }
}
