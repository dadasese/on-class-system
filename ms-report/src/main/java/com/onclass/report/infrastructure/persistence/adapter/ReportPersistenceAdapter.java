package com.onclass.report.infrastructure.persistence.adapter;

import com.onclass.report.domain.model.BootcampReport;
import com.onclass.report.domain.spi.IReportPersistencePort;
import com.onclass.report.infrastructure.persistence.entity.BootcampReportDocument;
import com.onclass.report.infrastructure.persistence.mapper.ReportDocumentMapper;
import com.onclass.report.infrastructure.persistence.repository.IBootcampReportRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class ReportPersistenceAdapter implements IReportPersistencePort {
    private final IBootcampReportRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ReportDocumentMapper mapper;

    public ReportPersistenceAdapter(IBootcampReportRepository repository,
                                     ReactiveMongoTemplate mongoTemplate,
                                     ReportDocumentMapper mapper) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
    }

    @Override
    public Mono<BootcampReport> save(BootcampReport report) {
        return repository.save(mapper.toDocument(report)).map(mapper::toDomain);
    }

    @Override
    public Mono<BootcampReport> findByBootcampId(Long bootcampId) {
        return repository.findByBootcampId(bootcampId).map(mapper::toDomain);
    }

    @Override
    public Mono<BootcampReport> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<BootcampReport> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Flux<BootcampReport> findAllSortedByPersonaCountDesc(int page, int size) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "person_count"))
                .skip((long) page * size)
                .limit(size);

        return mongoTemplate.find(query, BootcampReportDocument.class)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<BootcampReport> findTopByPersonaCount() {
        return repository.findFirstByOrderByPersonCountDesc()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }

    @Override
    public Mono<Void> deleteByBootcampId(Long bootcampId) {
        return repository.deleteByBootcampId(bootcampId);
    }

    /**
     * Upsert: if a report for bootcampId exists, update it;
     * otherwise insert a new one. Preserves the original reportedAt.
     */
    @Override
    public Mono<BootcampReport> upsertByBootcampId(BootcampReport report) {
        return repository.findByBootcampId(report.getBootcampId())
                .flatMap(existing -> {
                    BootcampReportDocument doc = mapper.toDocument(report);
                    doc.setId(existing.getId());
                    doc.setReportedAt(existing.getReportedAt()); // Keep original
                    doc.setUpdatedAt(LocalDateTime.now());
                    return repository.save(doc);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    BootcampReportDocument doc = mapper.toDocument(report);
                    doc.setReportedAt(LocalDateTime.now());
                    doc.setUpdatedAt(LocalDateTime.now());
                    return repository.save(doc);
                }))
                .map(mapper::toDomain);
    }
}
