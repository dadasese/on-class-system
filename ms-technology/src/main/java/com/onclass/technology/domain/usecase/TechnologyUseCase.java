package com.onclass.technology.domain.usecase;

import com.onclass.technology.domain.api.ITechnologyServicePort;
import com.onclass.technology.domain.exception.InvalidTechnologyException;
import com.onclass.technology.domain.exception.TechnologyAlreadyExistsException;
import com.onclass.technology.domain.exception.TechnologyNotFoundException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.spi.ITechnologyPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class TechnologyUseCase implements ITechnologyServicePort {

    private final ITechnologyPersistencePort persistencePort;

    @Override
    public Mono<Technology> createTechnology(Technology technology) {
        return validate(technology)
                .then(Mono.defer(() -> persistencePort.existByName(technology.getName())))  // ← Lazy!
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new TechnologyAlreadyExistsException(technology.getName()));
                    }
                    return persistencePort.save(technology);
                });
    }

    @Override
    public Mono<Technology> getTechnologyById(Long id) {
        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new TechnologyNotFoundException("Technology not found by id: " + id)));
    }

    @Override
    public Mono<Technology> getTechnologyByName(String name) {
        return persistencePort.findByName(name)
                .switchIfEmpty(Mono.error(new TechnologyNotFoundException("Technology not found by name: " + name)));
    }

    @Override
    public Flux<Technology> getAllTechnologies(int page, int size, String sortBy, String sortDirection) {
        return persistencePort.findAll(page, size, sortBy, sortDirection);
    }

    @Override
    public Mono<Long> countTechnologies() {
        return persistencePort.count();
    }

    @Override
    public Mono<Technology> updateTechnology(Long id, Technology technology) {
        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new TechnologyNotFoundException(id)))
                .flatMap(existingTechnology ->
                        validateNameForUpdate(existingTechnology, technology.getName())
                                .then(Mono.defer(() -> {
                                    Technology updatedTechnology = new Technology (
                                            existingTechnology.getId(),
                                            technology.getName(),
                                            technology.getDescription()
                                    );
                                    return persistencePort.update(updatedTechnology);
                                }))
                );
    }

    @Override
    public Mono<Void> deleteTechnology(Long id) {
        return persistencePort.findById(id)
                .switchIfEmpty(Mono.error(new TechnologyNotFoundException(id)))
                .flatMap(technology -> persistencePort.deleteById(id));
    }

    @Override
    public Flux<Technology> getTechnologiesByIds(List<Long> ids) {
        return persistencePort.findAllByIds(ids);
    }

    private Mono<Void> validateNameForUpdate(Technology existingTechnology, String  newTechnologyName) {
        if (existingTechnology.getName().equalsIgnoreCase(newTechnologyName)){
            return Mono.empty();
        }

        return persistencePort.existByName(newTechnologyName)
                .flatMap(exists -> {
                    if (exists){
                        return Mono.error(new TechnologyAlreadyExistsException(newTechnologyName));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validate(Technology technology) {
        if (technology.getName() == null || technology.getName().isBlank()) {
            return Mono.error(new InvalidTechnologyException("Name is required"));
        }
        if (technology.getName().length() > 50) {
            return Mono.error(new InvalidTechnologyException("Name must not exceed 50 characters"));
        }
        if (technology.getDescription() == null || technology.getDescription().isBlank()) {
            return Mono.error(new InvalidTechnologyException("Description is required"));
        }
        if (technology.getDescription().length() > 90) {
            return Mono.error(new InvalidTechnologyException("Description must not exceed 90 characters"));
        }
        return Mono.empty();
    }
}
