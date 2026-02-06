package com.onclass.bootcamp.infrastructure.persistence.adapter;

import com.onclass.bootcamp.domain.exception.BootcampNotFoundException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.infrastructure.entity.BootcampCapacityEntity;
import com.onclass.bootcamp.infrastructure.entity.BootcampEntity;
import com.onclass.bootcamp.infrastructure.persistence.function.SortStrategy;
import com.onclass.bootcamp.infrastructure.persistence.mapper.BootcampEntityMapper;
import com.onclass.bootcamp.infrastructure.persistence.repository.IBootcampCapacityRepository;
import com.onclass.bootcamp.infrastructure.persistence.repository.IBootcampRepository;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class BootcampPersistenceAdapter implements IBootcampPersistencePort {

    private final IBootcampRepository repository;
    private final IBootcampCapacityRepository bootcampCapacityRepository;
    private final BootcampEntityMapper mapper;
    private final DatabaseClient databaseClient;


    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return repository.save(mapper.toEntity(bootcamp))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Flux<Bootcamp> findAllPaginated(int page, int size, String sortBy, String sortDir) {
        return SortStrategy.resolve(sortBy, sortDir)
                .execute(repository, page * size, size)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(id)))
                .flatMap(e -> repository.deleteById(e.getId()));
    }

    @Override
    public Mono<Void> saveCapacityRelations(Long bootcampId, List<Long> capacityIds) {
        return bootcampCapacityRepository.deleteByBootcampId(bootcampId)
                .thenMany(Flux.fromIterable(capacityIds)
                        .map(capIds -> new BootcampCapacityEntity(bootcampId, capIds))
                        .flatMap(bootcampCapacityRepository::save))
                .then();
    }

    @Override
    public Mono<Void> saveCapacityRelation(Long bootcampId, Long capacityId) {
        return Mono.fromRunnable(() ->
                databaseClient.sql("INSERT INTO bootcamp_capacity (bootcamp_id, capacity_id) VALUES (:bootcampId, :capacityId)")
                        .bind("bootcampId", bootcampId)
                        .bind("capacityId", capacityId)
                        .fetch()
                        .rowsUpdated()
                        .subscribe()
        );
    }

    @Override
    public Flux<Long> findCapacityIdsByBootcampId(Long bootcampId) {
        return bootcampCapacityRepository.findCapacityIdsByBootcampId(bootcampId);
    }

    @Override
    public Mono<Void> deleteCapacityRelationsByBootcampId(Long bootcampId) {
        return bootcampCapacityRepository.deleteByBootcampId(bootcampId);
    }

    @Override
    public Mono<Long> countBootcampsByCapacityId(Long capacityId) {
        return bootcampCapacityRepository.countByCapacityId(capacityId);
    }

    @Override
    public Mono<Long> countBootcampsByTechnologyId(Long technologyId) {
        return bootcampCapacityRepository.countByTechnologyId(technologyId);
    }
}
