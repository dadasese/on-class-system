package com.onclass.capacity.domain.usecase;

import com.onclass.capacity.domain.api.ICapacityServicePort;
import com.onclass.capacity.domain.exception.CapacityAlreadyExistsException;
import com.onclass.capacity.domain.exception.CapacityNotFoundException;
import com.onclass.capacity.domain.exception.DuplicateTechnologyException;
import com.onclass.capacity.domain.exception.TechnologyNotFoundException;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.CapacityWithTechnologies;
import com.onclass.capacity.domain.spi.ICapacityPersistencePort;
import com.onclass.capacity.domain.spi.ITechnologyClientPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@AllArgsConstructor
public class CapacityUseCase implements ICapacityServicePort {

    private final ICapacityPersistencePort persistencePort;
    private final ITechnologyClientPort technologyClientPort;

    @Override
    public Mono<CapacityWithTechnologies> create(Capacity capacity) {
        capacity.validate();
        validateNoDuplicateTechnologies(capacity.getTechnologyIds());
        return  persistencePort.existsByName(capacity.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new CapacityAlreadyExistsException(capacity.getName()));
                    }
                    return validateTechnologyExist(capacity.getTechnologyIds());
                })
                .then(persistencePort.save(capacity))
                .flatMap(saved ->
                    persistencePort.saveTechnologyRelation(saved.getId(),
                                    capacity.getTechnologyIds())
                            .thenReturn(saved)
                ).flatMap(this::toCapacityWithTechnologies);
    }

    private Mono<Void> validateTechnologyExist(List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .flatMap(id -> technologyClientPort.existsById(id)
                        .flatMap(exists -> {
                            if (!exists){
                                return Mono.error(new TechnologyNotFoundException(id));
                            }
                            return Mono.empty();
                        })
                ).then();
    }

    @Override
    public Mono<CapacityWithTechnologies> findById(Long id) {
        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new CapacityNotFoundException(id)))
                .flatMap(this::toCapacityWithTechnologies);
    }

    @Override
    public Flux<CapacityWithTechnologies> findAllPaginated(int page, int size, String sortBy, String sortDir) {
        return persistencePort.findAllPaginated(page, size, sortBy, sortDir)
                .concatMap(this::toCapacityWithTechnologies);
    }

    private Mono<CapacityWithTechnologies> toCapacityWithTechnologies(Capacity capacity){
        return persistencePort.findTechnologyByCapacityId(capacity.getId())
                .collectList()
                .flatMap(ids -> technologyClientPort.findByIds(ids).collectList())
                .map(technologies ->
                        new CapacityWithTechnologies(
                                capacity.getId(),
                                capacity.getName(),
                                capacity.getDescription(),
                                technologies,
                                technologies.size()
                        ));
    }

    @Override
    public Mono<Capacity> update(Long id, Capacity capacity) {
        capacity.validate();

        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new CapacityNotFoundException(id)))
                .flatMap(existing -> {
                    if (!existing.getName().equalsIgnoreCase(capacity.getName())){
                        return persistencePort.existsByName(capacity.getName())
                                .flatMap(exists -> {
                                    if (exists){
                                        return Mono.error(
                                                new CapacityAlreadyExistsException(capacity.getName()));
                                    }
                                    return Mono.just(existing);
                                });
                    }
                    return Mono.just(existing);
                })
                .flatMap(existing -> validateTechnologyExist(capacity.getTechnologyIds())
                        .thenReturn(existing))
                .flatMap(existing -> {
                    Capacity updated = Capacity
                            .builder()
                            .name(capacity.getName())
                            .description(capacity.getDescription())
                            .technologyIds(capacity.getTechnologyIds())
                            .build();
                    return persistencePort.save(updated);
                })
                .flatMap(saved ->
                        persistencePort.saveTechnologyRelation(saved.getId(), capacity.getTechnologyIds())
                                .thenReturn(saved));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new CapacityNotFoundException(id)))
                .flatMap(existing -> {
                    log.info("Starting transaction delete for capacity id = {}", id);

                    return persistencePort.findTechnologyByCapacityId(id)
                            .collectList()
                            .flatMap(technologyIds -> persistencePort.deleteTechnologyRelationsByCapacityId(id)
                                    .then(persistencePort.deleteById(id))
                                    .then(deleteOrphanedTechnologies(technologyIds))
                            );
                })
                .doOnSuccess(v -> log.info("Capacity id={} deleted successfully", id));
    }

    @Override
    public Mono<Long> count() {
        return persistencePort.count();
    }

    private Mono<Void> deleteOrphanedTechnologies(List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .flatMap(technologyId ->
                        persistencePort.countTechnologyByCapacityId(technologyId)
                                .flatMap(count -> {
                                    if (count == 0) {
                                        log.info("Technology id={} is orphaned requesting deletion", technologyIds);
                                        return technologyClientPort.deleteById(technologyId)
                                                .onErrorResume(ex -> {
                                                    log.error("Failed to delete orphaned technology {}:{}", technologyId, ex.getMessage());
                                                    return Mono.empty();
                                                });
                                    }
                                    log.debug("Technology Id = {} still referenced by {} by bootcamp(s)", technologyId, count);
                                    return Mono.empty();
                                })
                ).then();
    }

    private void validateNoDuplicateTechnologies(List<Long> technologyIds) {
        Set<Long> uniqueIds = new HashSet<>(technologyIds);
        if (uniqueIds.size() != technologyIds.size()) {
            List<Long> duplicates = technologyIds.stream()
                    .filter(id -> Collections.frequency(technologyIds, id) > 1)
                    .distinct()
                    .toList();
            throw new DuplicateTechnologyException(
                    "Duplicate technology IDs not allowed: " + duplicates);
        }
    }
}
