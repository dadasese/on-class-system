package com.onclass.capacity.infrastructure.persistence.function;

import com.onclass.capacity.infrastructure.persistence.entity.CapacityEntity;
import com.onclass.capacity.infrastructure.persistence.repository.ICapacityRepository;
import org.apache.commons.lang3.function.TriFunction;
import reactor.core.publisher.Flux;

public enum SortStrategy {
    NAME_ASC(ICapacityRepository::findAllByNameAsc),
    NAME_DESC(ICapacityRepository::findAllByNameDesc),
    TECH_COUNT_ASC(ICapacityRepository::findAllByTechnologyCountAsc),
    TECH_COUNT_DESC(ICapacityRepository::findAllByTechnologyCountDesc);

    private final TriFunction<ICapacityRepository, Integer, Integer, Flux<CapacityEntity>> queryFunction;

    SortStrategy(TriFunction<ICapacityRepository, Integer, Integer, Flux<CapacityEntity>> queryFunction) {
        this.queryFunction = queryFunction;
    }

    public Flux<CapacityEntity> execute(ICapacityRepository repository, int offset, int size) {
        return queryFunction.apply(repository, offset, size);
    }

    public static SortStrategy resolve(String sortBy, String sortDir) {
        boolean asc = "asc".equalsIgnoreCase(sortDir);
        boolean sortByTechCount = "technologies".equalsIgnoreCase(sortBy)
                || "technologyCount".equalsIgnoreCase(sortBy);

        if (sortByTechCount) {
            return asc ? TECH_COUNT_ASC : TECH_COUNT_DESC;
        }
        return asc ? NAME_ASC : NAME_DESC;
    }
}