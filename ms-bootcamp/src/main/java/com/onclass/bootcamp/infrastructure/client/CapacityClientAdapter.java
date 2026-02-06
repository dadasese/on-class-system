package com.onclass.bootcamp.infrastructure.client;

import com.onclass.bootcamp.domain.exception.CapacityServiceException;
import com.onclass.bootcamp.domain.model.CapacityInfo;
import com.onclass.bootcamp.domain.model.TechnologyInfo;
import com.onclass.bootcamp.domain.spi.ICapacityClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class CapacityClientAdapter implements ICapacityClientPort {

    private static final Logger log = LoggerFactory.getLogger(CapacityClientAdapter.class);
    private static final String PATH = "/api/v1/capabilities";

    private final WebClient webClient;

    public CapacityClientAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return webClient.get()
                .uri(PATH + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .map(r -> true)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) return Mono.just(false);
                    return Mono.error(new CapacityServiceException(ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new CapacityServiceException("Service unavailable")));

    }

    @Override
    public Mono<CapacityInfo> findById(Long id) {
        return webClient.get()
                .uri(PATH + "/{id}", id)
                .retrieve()
                .bodyToMono(CapacityApiResponse.class)
                .map(this::toInfo)
                .onErrorResume(WebClientResponseException.NotFound.class, ex ->
                        Mono.empty())
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new CapacityServiceException(ex.getMessage())));
    }

    @Override
    public Flux<CapacityInfo> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Flux.empty();

        return Flux.fromIterable(ids)
                .flatMap(this::findById)
                .filter(Objects::nonNull);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return webClient.delete()
                .uri(PATH + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .then()
                .doOnSuccess(v -> log.info("Deleted capacity {}, through Capacity service", id))
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.error("Failed to delete capacity {}, through capacity service: {}", id, ex.getMessage());
                    return Mono.error(new CapacityServiceException(ex.getMessage()));
                });
    }

    private CapacityInfo toInfo(CapacityApiResponse r){
        List<TechnologyInfo> technologies = r.technologies() != null ?
                r.technologies.stream()
                        .map(t -> new TechnologyInfo(t.id(), t.name()))
                        .toList() : List.of();
        return new CapacityInfo(r.id(), r.name(), r.description(), technologies, technologies.size());
    }

    private record CapacityApiResponse(
            Long id,
            String name,
            String description,
            List<TechnologyApiResponse> technologies,
            int technologyCount
    ) {}

    private record TechnologyApiResponse(
            Long id, String name
    ) {}
}
