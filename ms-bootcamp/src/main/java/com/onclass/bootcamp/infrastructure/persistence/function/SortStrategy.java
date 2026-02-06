package com.onclass.bootcamp.infrastructure.persistence.function;


import com.onclass.bootcamp.infrastructure.entity.BootcampEntity;
import com.onclass.bootcamp.infrastructure.persistence.repository.IBootcampRepository;
import org.apache.commons.lang3.function.TriFunction;
import reactor.core.publisher.Flux;

public enum SortStrategy {
    NAME_ASC(IBootcampRepository::findAllByNameAsc),
    NAME_DESC(IBootcampRepository::findAllByNameDesc),
    TECH_COUNT_ASC(IBootcampRepository::findAllByCapacityCountAsc),
    TECH_COUNT_DESC(IBootcampRepository::findAllByCapacityCountDesc);

    private final TriFunction<IBootcampRepository, Integer, Integer, Flux<BootcampEntity>> queryFunction;

    SortStrategy(TriFunction<IBootcampRepository, Integer, Integer, Flux<BootcampEntity>> queryFunction) {
        this.queryFunction = queryFunction;
    }

    public Flux<BootcampEntity> execute(IBootcampRepository repository, int offset, int size) {
        return queryFunction.apply(repository, offset, size);
    }

    public static SortStrategy resolve(String sortBy, String sortDir) {
        boolean asc = "asc".equalsIgnoreCase(sortDir);
        boolean sortByTechCount = "capacities".equalsIgnoreCase(sortBy)
                || "capacitiesCount".equalsIgnoreCase(sortBy);

        if (sortByTechCount) {
            return asc ? TECH_COUNT_ASC : TECH_COUNT_DESC;
        }
        return asc ? NAME_ASC : NAME_DESC;
    }
}