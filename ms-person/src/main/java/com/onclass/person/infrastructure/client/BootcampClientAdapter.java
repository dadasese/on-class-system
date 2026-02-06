package com.onclass.person.infrastructure.client;

import com.onclass.person.domain.model.BootcampInfo;
import com.onclass.person.domain.exception.BootcampServiceException;
import com.onclass.person.domain.spi.IBootcampClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Component
public class BootcampClientAdapter implements IBootcampClientPort {

    private static final Logger log = LoggerFactory.getLogger(BootcampClientAdapter.class);
    private static final String PATH = "/api/v1/bootcamps";

    private final WebClient webClient;

    public BootcampClientAdapter(WebClient bootcampWebClient) {
        this.webClient = bootcampWebClient;
    }

    @Override
    public Mono<BootcampInfo> findById(Long id) {
        return webClient.get()
                .uri(PATH + "/{id}", id)
                .retrieve()
                .bodyToMono(BootcampApiResponse.class)
                .map(this::toInfo)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.warn("Bootcamp {} not found in bootcamp service", id);
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error fetching bootcamp {}: {}", id, ex.getMessage());
                    return Mono.error(new BootcampServiceException(ex.getMessage()));
                });
    }

    @Override
    public Flux<BootcampInfo> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Flux.empty();
        return Flux.fromIterable(ids)
                .flatMap(this::findById)
                .filter(java.util.Objects::nonNull);
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
                    return Mono.error(new BootcampServiceException(ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new BootcampServiceException("Service unavailable")));
    }

    private BootcampInfo toInfo(BootcampApiResponse r) {
        return new BootcampInfo(r.id(), r.name(), r.description(),
                r.startDate() != null ? LocalDate.parse(r.startDate()) : null,
                r.weeksDuration());
    }

    private record BootcampApiResponse(
            Long id, String name, String description,
            String startDate, Integer weeksDuration
    ) {}
}
