package com.onclass.bootcamp.application.handler;

import com.onclass.bootcamp.application.dto.request.BootcampRequest;
import com.onclass.bootcamp.application.dto.response.PageResponse;
import com.onclass.bootcamp.application.mapper.BootcampDtoMapper;
import com.onclass.bootcamp.domain.api.IBootcampServicePort;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class BootcampHandler {
    private final IBootcampServicePort servicePort;
    private final BootcampDtoMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(BootcampRequest.class)
                .flatMap(this::validate)
                .map(mapper::toDomain)
                .flatMap(servicePort::create)
                .map(mapper::toResponse)
                .flatMap(r -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(r));
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return servicePort.findById(id)
                .map(mapper::toResponse)
                .flatMap(r -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(r));
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String sortDir = request.queryParam("sortDir").orElse("asc");

        return servicePort.findAllPaginated(page, size, sortBy, sortDir)
                .map(mapper::toResponse)
                .collectList()
                .zipWith(servicePort.count())
                .flatMap(tuple -> {
                    var content = tuple.getT1();
                    var total = tuple.getT2();
                    var pageResponse = new PageResponse<>(content, page, size, total, (int) Math.ceil((double) total / size));
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(pageResponse);
                });
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(BootcampRequest.class)
                .flatMap(this::validate)
                .map(mapper::toDomain)
                .flatMap(b -> servicePort.update(id, b))
                .map(mapper::toSimpleResponse)
                .flatMap(r -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(r));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return servicePort.delete(id)
                .then(ServerResponse.noContent().build());
    }



    private <T> Mono<T> validate(T req) {
        var violations = validator.validate(req);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a, b) -> a + "; " + b).orElse("Validation failed");
            return Mono.error(new IllegalArgumentException(msg));
        }
        return Mono.just(req);
    }
}
