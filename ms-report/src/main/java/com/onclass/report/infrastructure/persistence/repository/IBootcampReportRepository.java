package com.onclass.report.infrastructure.persistence.repository;

import com.onclass.report.infrastructure.persistence.entity.BootcampReportDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBootcampReportRepository extends ReactiveMongoRepository<BootcampReportDocument, String> {

    Mono<BootcampReportDocument> findByBootcampId(Long bootcampId);

    Mono<Void> deleteByBootcampId(Long bootcampId);

    Mono<Boolean> existsByBootcampId(Long bootcampId);

    Flux<BootcampReportDocument> findAllByOrderByPersonCountDesc();

    Mono<BootcampReportDocument> findFirstByOrderByPersonCountDesc();
}
