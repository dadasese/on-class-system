package com.onclass.technology.infrastructure.output.persistence.adapter;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.spi.ITechnologyPersistencePort;
import com.onclass.technology.infrastructure.output.persistence.entity.TechnologyEntity;
import com.onclass.technology.infrastructure.output.persistence.mapper.ITechnologyEntityMapper;
import com.onclass.technology.infrastructure.output.persistence.repository.ITechnologyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TechnologyPersistenceAdapter implements ITechnologyPersistencePort {
    private static final Logger log = LoggerFactory.getLogger(TechnologyPersistenceAdapter.class);

    private static final String SORT_ASC = "ASC";

    private final ITechnologyRepository repository;
    private final ITechnologyEntityMapper mapper;

    public TechnologyPersistenceAdapter(ITechnologyRepository repository,
                                        ITechnologyEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public Mono<Technology> save(Technology technology) {
        log.debug("Saving technology: {}", technology.getName());

        TechnologyEntity entity = mapper.toEntity(technology);

        return repository.save(entity)
                .map(mapper::toDomain)
                .doOnSuccess(saved -> log.info("Technology saved with id: {}", saved.getId()))
                .doOnError(error -> log.error("Error saving technology: {}", error.getMessage()));
    }

    /**
     * {@inheritDoc}
     * Finds an active technology by its ID.
     */
    @Override
    public Mono<Technology> findById(Long id) {
        log.debug("Finding technology by id: {}", id);

        return repository.findById(id)
                .map(mapper::toDomain)
                .doOnSuccess(tech -> {
                    if (tech != null) {
                        log.debug("Found technology: {}", tech.getId());
                    } else {
                        log.debug("Technology not found with id: {}", id);
                    }
                });
    }

    /**
     * {@inheritDoc}
     * Finds an active technology by its name.
     */
    @Override
    public Mono<Technology> findByName(String nombre) {
        log.debug("Finding technology by name: {}", nombre);

        return repository.findByName(nombre)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return repository.countByName(name)
                .map(count -> count > 0)
                .defaultIfEmpty(false);
    }

    @Override
    public Flux<Technology> findAll(int page, int size, String sortBy, String sortDir) {
        log.debug("Finding all technologies - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        long offset = (long) page * size;

        // Determine sort direction and call appropriate repository method
        Flux<TechnologyEntity> entities;
        if (SORT_ASC.equalsIgnoreCase(sortDir)) {
            entities = repository.findAllPaginatedAsc(size, offset);
        } else {
            entities = repository.findAllPaginatedDesc(size, offset);
        }

        return entities.map(mapper::toDomain);
    }

    @Override
    public Mono<Long> count() {
        log.debug("Counting all active technologies");

        return repository.count();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Soft deleting technology with id: {}", id);

        return repository.deleteById(id)
                .doOnSuccess(v -> log.info("Technology with id {} soft deleted", id))
                .doOnError(error -> log.error("Error deleting technology: {}", error.getMessage()));
    }

    @Override
    public Flux<Technology> findAllByIds(List<Long> ids) {
        log.debug("Finding technologies by ids: {}", ids);

        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }

        return repository.findAllByIdIn(ids)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Technology> update(Technology technology) {
        log.debug("Updating technology with id: {}", technology.getId());

        return repository.findById(technology.getId())
                .flatMap(existingEntity -> {
                    // Update only the business fields, preserve metadata
                    existingEntity.setName(technology.getName());
                    existingEntity.setDescription(technology.getDescription());

                    return repository.save(existingEntity);
                })
                .map(mapper::toDomain)
                .doOnSuccess(updated -> log.info("Technology updated: {}", updated.getId()))
                .doOnError(error -> log.error("Error updating technology: {}", error.getMessage()));
    }
}
