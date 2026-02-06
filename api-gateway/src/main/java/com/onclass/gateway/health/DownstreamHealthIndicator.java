package com.onclass.gateway.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Map;

@Component("downstreamServices")
public class DownstreamHealthIndicator implements ReactiveHealthIndicator {

    private final Map<String, String> services;
    private final WebClient webClient;

    public DownstreamHealthIndicator(
            @Value("${MS_TECHNOLOGY_URL:http://localhost:8081}") String techUrl,
            @Value("${MS_CAPACITY_URL:http://localhost:8082}") String capUrl,
            @Value("${MS_BOOTCAMP_URL:http://localhost:8083}") String bootUrl,
            @Value("${MS_PERSON_URL:http://localhost:8084}") String persUrl,
            @Value("${MS_REPORT_URL:http://localhost:8085}") String repUrl) {
        this.webClient = WebClient.create();
        this.services = Map.of(
                "ms-technology", techUrl,
                "ms-capacity", capUrl,
                "ms-bootcamp", bootUrl,
                "ms-person", persUrl,
                "ms-report", repUrl
        );
    }

    @Override
    public Mono<Health> health() {
        return Flux.fromIterable(services.entrySet())
                .flatMap(entry -> checkService(entry.getKey(), entry.getValue()))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(details -> {
                    boolean allUp = details.values().stream()
                            .allMatch("UP"::equals);
                    Health.Builder builder = allUp ? Health.up() : Health.down();
                    details.forEach(builder::withDetail);
                    return builder.build();
                });
    }

    private Mono<Map.Entry<String, String>> checkService(String name, String url) {
        return webClient.get()
                .uri(url + "/actuator/health")
                .retrieve()
                .toBodilessEntity()
                .map(r -> Map.entry(name, "UP"))
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(Map.entry(name, "DOWN")));
    }
}
