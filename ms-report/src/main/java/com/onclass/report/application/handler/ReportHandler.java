package com.onclass.report.application.handler;

import com.onclass.report.application.dto.request.BootcampEventRequest;
import com.onclass.report.application.dto.response.PageResponse;
import com.onclass.report.application.mapper.ReportDtoMapper;
import com.onclass.report.domain.api.IReportServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
public class ReportHandler {
    private final IReportServicePort service;
    private final ReportDtoMapper mapper;

    public ReportHandler(IReportServicePort service, ReportDtoMapper mapper) {
        this.service = service; this.mapper = mapper;
    }

    // HU-008: webhook — async report generation
    public Mono<ServerResponse> handleBootcampEvent(ServerRequest req) {
        return req.bodyToMono(BootcampEventRequest.class)
                .flatMap(event -> service.handleBootcampCreatedEvent(event.bootcampId()))
                .then(ServerResponse.accepted()
                        .bodyValue(Map.of("status", "accepted",
                                "message", "Report generation started")));
    }

    // HU-008: synchronous report generation (manual trigger)
    public Mono<ServerResponse> generateReport(ServerRequest req) {
        Long bootcampId = Long.parseLong(req.pathVariable("bootcampId"));
        return service.generateReport(bootcampId)
                .map(mapper::toResponse)
                .flatMap(r -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(r));
    }

    // HU-009: most popular bootcamp
    public Mono<ServerResponse> findMostPopular(ServerRequest req) {
        return service.findMostPopularBootcamp()
                .map(mapper::toResponse)
                .flatMap(r -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(r));
    }

    public Mono<ServerResponse> findByBootcampId(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("bootcampId"));
        return service.findByBootcampId(id)
                .map(mapper::toResponse)
                .flatMap(r -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(r));
    }

    public Mono<ServerResponse> findAll(ServerRequest req) {
        int page = Integer.parseInt(req.queryParam("page").orElse("0"));
        int size = Integer.parseInt(req.queryParam("size").orElse("10"));
        return service.findAllSortedByPopularity(page, size)
                .map(mapper::toResponse).collectList()
                .zipWith(service.count())
                .flatMap(t -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new PageResponse<>(t.getT1(), page, size, t.getT2(),
                                (int) Math.ceil((double) t.getT2() / size))));
    }

    public Mono<ServerResponse> handleBootcampUpdated(ServerRequest req) {
        return req.bodyToMono(BootcampEventRequest.class)
                .flatMap(event -> {
                    service.handleBootcampCreatedEvent(event.bootcampId());
                    return ServerResponse.accepted()
                            .bodyValue(Map.of(
                                    "status", "accepted",
                                    "message", "Report refresh started"
                            ));
                });
    }
}
