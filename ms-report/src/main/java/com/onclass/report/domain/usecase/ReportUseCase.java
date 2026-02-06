package com.onclass.report.domain.usecase;

import com.onclass.report.domain.api.IReportServicePort;
import com.onclass.report.domain.exception.BootcampDataException;
import com.onclass.report.domain.exception.ReportNotFoundException;
import com.onclass.report.domain.model.*;
import com.onclass.report.domain.spi.IBootcampDataCollectorPort;
import com.onclass.report.domain.spi.IReportPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReportUseCase implements IReportServicePort {
    private static final Logger log = LoggerFactory.getLogger(ReportUseCase.class);

    private final IReportPersistencePort persistence;
    private final IBootcampDataCollectorPort dataCollector;

    public ReportUseCase(IReportPersistencePort persistence,
                          IBootcampDataCollectorPort dataCollector) {
        this.persistence = persistence;
        this.dataCollector = dataCollector;
    }

    @Override
    public Mono<BootcampReport> generateReport(Long bootcampId) {
        log.info("Generating report for bootcamp {}", bootcampId);

        return dataCollector.fetchBootcampData(bootcampId)
                .switchIfEmpty(Mono.error(
                        new BootcampDataException("Bootcamp " + bootcampId + " not found")))
                .flatMap(bootcampData ->
                        dataCollector.fetchEnrolledPersons(bootcampId)
                                .collectList()
                                .map(person -> buildReport(bootcampData, person))
                )
                .flatMap(persistence::upsertByBootcampId)
                .doOnSuccess(r -> log.info(
                        "Report generated for bootcamp {} — caps={}, techs={}, personas={}",
                        bootcampId, r.getCapacityCount(),
                        r.getTechnologyCount(), r.getPersonCount()))
                .onErrorResume(ex -> {
                    log.error("Failed to generate report for bootcamp {}: {}",
                            bootcampId, ex.getMessage());
                    return Mono.error(ex);
                });
    }

    @Override
    public Mono<BootcampReport> findMostPopularBootcamp() {
        return persistence.findTopByPersonaCount()
                .switchIfEmpty(Mono.error(
                        new ReportNotFoundException(0L)));
    }

    @Override
    public Mono<BootcampReport> findByBootcampId(Long bootcampId) {
        return persistence.findByBootcampId(bootcampId)
                .switchIfEmpty(Mono.error(new ReportNotFoundException(bootcampId)));
    }
    @Override
    public Flux<BootcampReport> findAllSortedByPopularity(int page, int size) {
        return persistence.findAllSortedByPersonaCountDesc(page, size);
    }

    @Override
    public Mono<Long> count() {
        return persistence.count();
    }

    @Override
    public Mono<Void> handleBootcampCreatedEvent(Long bootcampId) {
        log.info("Received bootcamp-created event for bootcamp {}", bootcampId);

        // Fire and forget: subscribe on bounded elastic scheduler
        generateReport(bootcampId)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        report -> log.info("Async report generated for bootcamp {}", bootcampId),
                        error -> log.error("Async report generation failed for bootcamp {}: {}",
                                bootcampId, error.getMessage())
                );

        return Mono.empty(); // Return immediately — non-blocking
    }

    private BootcampReport buildReport(BootcampExternalData data,
                                       List<PersonSnapshot> personas) {

        List<CapacitySnapshot> caps = data.capacities() != null
                ? data.capacities().stream()
                .map(c -> new CapacitySnapshot(
                        c.id(), c.name(), c.description(),
                        c.technologies() != null
                                ? c.technologies().stream()
                                .map(t -> new TechnologySnapshot(t.id(), t.name()))
                                .toList()
                                : List.of(),
                        c.technologyCount()))
                .toList()
                : List.of();

        // Count total technologies across all capabilities
        int totalTechs = caps.stream()
                .mapToInt(CapacitySnapshot::technologyCount)
                .sum();

        BootcampReport report = new BootcampReport();
        report.setBootcampId(data.id());
        report.setName(data.name());
        report.setDescription(data.description());
        report.setStartDate(
                data.startDate() != null ? LocalDate.parse(data.startDate()) : null);
        report.setWeeksDuration(data.weeksDuration());
        report.setCapacities(caps);
        report.setCapacityCount(caps.size());
        report.setTechnologyCount(totalTechs);
        report.setPersons(personas);
        report.setPersonCount(personas.size());
        report.setReportedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        return report;
    }
}
