package com.onclass.report.infrastructure.client;

import com.onclass.report.domain.exception.BootcampDataException;
import com.onclass.report.domain.model.BootcampExternalData;
import com.onclass.report.domain.model.CapacityExternalData;
import com.onclass.report.domain.model.PersonSnapshot;
import com.onclass.report.domain.model.TechnologyExternalData;
import com.onclass.report.domain.spi.IBootcampDataCollectorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BootcampDataCollectorAdapter implements IBootcampDataCollectorPort {
    private static final Logger log = LoggerFactory.getLogger(BootcampDataCollectorAdapter.class);

    private final WebClient bootcampClient;
    private final WebClient personClient;

    public BootcampDataCollectorAdapter(WebClient bootcampWebClient,
                                        WebClient personWebClient) {
        this.bootcampClient = bootcampWebClient;
        this.personClient = personWebClient;
    }

    @Override
    public Mono<BootcampExternalData> fetchBootcampData(Long bootcampId) {
        return bootcampClient.get()
                .uri("/api/v1/bootcamps/{id}", bootcampId)
                .retrieve()
                .bodyToMono(BootcampApiResponse.class)
                .map(this::toExternalData)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.warn("Bootcamp {} not found", bootcampId);
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error fetching bootcamp {}: {}", bootcampId, ex.getMessage());
                    return Mono.error(new BootcampDataException(ex.getMessage()));
                });
    }

    @Override
    public Flux<PersonSnapshot> fetchEnrolledPersons(Long bootcampId) {
        return personClient.get()
                .uri("/api/v1/persons/by-bootcamp/{bootcampId}", bootcampId)
                .retrieve()
                .bodyToFlux(PersonApiResponse.class)
                .map(p -> new PersonSnapshot(p.id(), p.name(), p.email()))
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error fetching persons for bootcamp {}: {}",
                            bootcampId, ex.getMessage());
                    return Flux.empty(); // Graceful: report with 0 personas
                });
    }

    private BootcampExternalData toExternalData(BootcampApiResponse r) {
        List<CapacityExternalData> caps = r.capacities() != null
                ? r.capacities().stream()
                .map(c -> new CapacityExternalData(
                        c.id(), c.name(), c.description(),
                        c.technologies() != null
                                ? c.technologies().stream()
                                .map(t -> new TechnologyExternalData(t.id(), t.name()))
                                .toList()
                                : List.of(),
                        c.technologyCount()))
                .toList()
                : List.of();

        return new BootcampExternalData(
                r.id(), r.name(), r.description(),
                r.startDate(), r.weeksDuration(), caps, caps.size());
    }
    private record BootcampApiResponse(
            Long id, String name, String description,
            String startDate, Integer weeksDuration,
            List<CapacityApiResponse> capacities, int capacityCount
    ) {}
    private record CapacityApiResponse(
            Long id, String name, String description,
            List<TechnologyApiResponse> technologies, int technologyCount
    ) {}
    private record TechnologyApiResponse(Long id, String name) {}
    private record PersonApiResponse(Long id, String name, String email) {}
}
