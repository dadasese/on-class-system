package com.onclass.person.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportEventPublisher {

    private final WebClient reporteWebClient;

    /**
     * Fire-and-forget: notifies MS-Report to regenerate report for each bootcamp.
     * Does not block enrollment flow.
     */
    public void publishEnrollmentCompleted(List<Long> bootcampIds) {
        Flux.fromIterable(bootcampIds)
                .flatMap(this::notifyReport)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        id -> log.debug("Report refresh triggered for bootcamp {}", id),
                        err -> log.warn("Failed to trigger some report refreshes: {}", err.getMessage())
                );
    }

    private Mono<Long> notifyReport(Long bootcampId) {
        return reporteWebClient.post()
                .uri("/api/v1/reports/events/bootcamp-updated")
                .bodyValue(Map.of("bootcampId", bootcampId))
                .retrieve()
                .toBodilessEntity()
                .thenReturn(bootcampId)
                .onErrorResume(ex -> {
                    log.warn("Failed to notify report for bootcamp {}: {}", bootcampId, ex.getMessage());
                    return Mono.just(bootcampId); // Don't fail the whole batch
                });
    }
}