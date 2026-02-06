package com.onclass.bootcamp.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
public class ReportEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ReportEventPublisher.class);
    private final WebClient webClient;

    public ReportEventPublisher(
            @Value("${ms-reporte.base-url:http://localhost:8080}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public void publishBootcampCreated(Long bootcampId) {
        webClient.post()
                .uri("/api/v1/reports/events/bootcamp-created")
                .bodyValue(Map.of("bootcampId", bootcampId))
                .retrieve()
                .toBodilessEntity()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        response -> log.info("Report event sent for bootcamp {}", bootcampId),
                        error -> log.warn("Failed to send report event for bootcamp {}: {}",
                                bootcampId, error.getMessage())
                );
    }
}

