package com.onclass.report.domain.api;

import com.onclass.report.domain.model.BootcampReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IReportServicePort {

    Mono<BootcampReport> generateReport(Long bootcampId);
    Mono<BootcampReport> findMostPopularBootcamp();
    Mono<BootcampReport> findByBootcampId(Long bootcampId);
    Flux<BootcampReport> findAllSortedByPopularity(int page, int size);
    Mono<Long> count();
    Mono<Void> handleBootcampCreatedEvent(Long bootcampId);

}
