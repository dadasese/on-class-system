package com.onclass.report.domain.spi;

import com.onclass.report.domain.model.BootcampReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IReportPersistencePort {
    Mono<BootcampReport> save(BootcampReport report);
    Mono<BootcampReport> findByBootcampId(Long bootcampId);
    Mono<BootcampReport> findById(String id);
    Flux<BootcampReport> findAll();
    Flux<BootcampReport> findAllSortedByPersonaCountDesc(int page, int size);
    Mono<BootcampReport> findTopByPersonaCount();   // HU-009
    Mono<Long> count();
    Mono<Void> deleteByBootcampId(Long bootcampId);
    Mono<BootcampReport> upsertByBootcampId(BootcampReport report);
}
