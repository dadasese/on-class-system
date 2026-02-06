package com.onclass.capacity.infrastructure.client;

import com.onclass.capacity.domain.exception.TechnologyServiceException;
import com.onclass.capacity.domain.model.TechnologyInfo;
import com.onclass.capacity.domain.spi.ITechnologyClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TechnologyClientAdapter implements ITechnologyClientPort {

    private static final Logger log =
            LoggerFactory.getLogger(TechnologyClientAdapter.class);
    private static final String TECHNOLOGIES_PATH = "/api/v1/technologies";

    private final WebClient webClient;

    public TechnologyClientAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return webClient.get()
                .uri(TECHNOLOGIES_PATH + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND){
                        return Mono.just(false);
                    }
                    log.error("Error checking technology existence: {}", ex.getMessage());
                    return Mono.error(new TechnologyServiceException(ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Connection error to MS-technology: {}", ex.getMessage());
                    return Mono.error(new TechnologyServiceException("service unavailable"));
                });
    }

    @Override
    public Flux<TechnologyInfo> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }

        String idsParam = String.join(",", ids.stream().map(String::valueOf).toList());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TECHNOLOGIES_PATH + "/ids")
                        .queryParam("ids", idsParam)
                        .build())
                .retrieve()
                .bodyToFlux(TechnologyResponse.class)
                .map(this::toTechnologyInfo)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error fetching technologies by ids: {}", ex.getMessage());
                    return Flux.error(new TechnologyServiceException(ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Connection error to MS-Technology: {}", ex.getMessage());
                    return Flux.error(new TechnologyServiceException("Service unavailable"));
                });
    }

    @Override
    public Mono<TechnologyInfo> findById(Long id) {
        return webClient.get()
                .uri(TECHNOLOGIES_PATH + "/{id}", id)
                .retrieve()
                .bodyToMono(TechnologyResponse.class)
                .map(this::toTechnologyInfo)
                .onErrorResume(WebClientResponseException.NotFound.class, ex ->
                        Mono.empty())
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error fetching technology {}:{}", id, ex.getMessage());
                    return Mono.error(new TechnologyServiceException(ex.getMessage()));
                });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return webClient.delete()
                .uri(TECHNOLOGIES_PATH + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .then()
                .doOnSuccess(v -> log.info("Deleted technology {}, through technology service", id))
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.error("Failed to delete technology {}, through technology service: {}", id, ex.getMessage());
                    return Mono.error(new TechnologyServiceException(ex.getMessage()));
                });
    }

    private TechnologyInfo toTechnologyInfo(TechnologyResponse response){
        return TechnologyInfo.of(response.id(), response.name());
    }

    private record TechnologyResponse(Long id, String name, String description) {}
}
