package com.onclass.capacity.infrastructure.persistence.adapter;

import com.onclass.capacity.domain.exception.CapacityNotFoundException;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.spi.ICapacityPersistencePort;
import com.onclass.capacity.infrastructure.persistence.entity.CapacityEntity;
import com.onclass.capacity.infrastructure.persistence.entity.CapacityTechnologyEntity;
import com.onclass.capacity.infrastructure.persistence.function.SortStrategy;
import com.onclass.capacity.infrastructure.persistence.mapper.ICapacityMapperEntity;
import com.onclass.capacity.infrastructure.persistence.repository.ICapacityRepository;
import com.onclass.capacity.infrastructure.persistence.repository.ICapacityTechnologyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class CapacityPersistenceAdapter implements ICapacityPersistencePort {

    private final ICapacityRepository repository;
    private final ICapacityTechnologyRepository technologyRepository;
    private final ICapacityMapperEntity mapper;

    @Override
    public Mono<Capacity> save(Capacity capacity) {
        return repository.save(mapper.toEntity(capacity))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Capacity> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Capacity> findByName(String name) {
        return repository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Flux<Capacity> findAllPaginated(int page, int size, String sortBy, String sortDir) {
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
                .switchIfEmpty(Mono.error(new CapacityNotFoundException(id)))
                .flatMap(repository::delete)
                .then();
    }

    @Override
    public Mono<Void> saveTechnologyRelation(Long capacityId, List<Long> technologyIds) {
        return technologyRepository.deleteByCapacityId(capacityId)
                .thenMany(Flux.fromIterable(technologyIds)
                        .map(technologyId ->
                                new CapacityTechnologyEntity(capacityId, technologyId))
                        .flatMap(technologyRepository::save))
                .then();
    }

    @Override
    public Mono<Void> deleteTechnologyRelationsByCapacityId(Long capacityId) {
        return technologyRepository.deleteByCapacityId(capacityId);
    }

    @Override
    public Flux<Long> findTechnologyByCapacityId(Long capacityId) {
        return technologyRepository.findTechnologyIdsByCapacityId(capacityId);
    }

    @Override
    public Mono<Integer> countTechnologyByCapacityId(Long capacityId) {
        return technologyRepository.countByCapacityId(capacityId);
    }
}
