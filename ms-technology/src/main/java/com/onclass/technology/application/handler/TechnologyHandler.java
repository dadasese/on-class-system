package com.onclass.technology.application.handler;


import com.onclass.technology.application.dto.request.TechnologyRequest;
import com.onclass.technology.application.dto.response.PageResponse;
import com.onclass.technology.application.dto.response.TechnologyResponse;
import com.onclass.technology.application.mapper.ITechnologyMapper;
import com.onclass.technology.domain.api.ITechnologyServicePort;
import com.onclass.technology.domain.exception.TechnologyAlreadyExistsException;
import com.onclass.technology.domain.exception.TechnologyNotFoundException;
import com.onclass.technology.domain.model.Technology;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TechnologyHandler {
    private static final Logger log = LoggerFactory.getLogger(TechnologyHandler.class);

    private static final String ID_PATH_VARIABLE = "id";
    private static final String NAME_PATH_VARIABLE = "name";
    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String SORT_BY_PARAM = "sortBy";
    private static final String SORT_DIR_PARAM = "sortDir";
    private static final String IDS_PARAM = "ids";

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final String DEFAULT_SORT_BY = "name";
    private static final String DEFAULT_SORT_DIR = "ASC";

    private final ITechnologyServicePort servicePort;
    private final ITechnologyMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> create(ServerRequest request)
    {
        log.debug("Handling create technology request");

        return request.bodyToMono(TechnologyRequest.class)
                .flatMap(this::validateRequest)
                .map(mapper::toDomain)
                .flatMap(servicePort::createTechnology)
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnSuccess( r -> log.info("Technology created successfully"))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable(ID_PATH_VARIABLE));
        log.debug("Handling find technology by id: {}", id);

        return servicePort.getTechnologyById(id)
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> findByName(ServerRequest request){
        String name = request.pathVariable(NAME_PATH_VARIABLE);
        log.debug("Handling find technology by name: {}", name);

        return servicePort.getTechnologyByName(name)
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        int page = request.queryParam(PAGE_PARAM)
                .map(Integer::parseInt)
                .orElse(DEFAULT_PAGE);

        int size = request.queryParam(SIZE_PARAM)
                .map(Integer::parseInt)
                .orElse(DEFAULT_SIZE);

        String sortBy = request.queryParam(SORT_BY_PARAM)
                .orElse(DEFAULT_SORT_BY);

        String sortDir = request.queryParam(SORT_DIR_PARAM)
                .map(String::toUpperCase)
                .orElse(DEFAULT_SORT_DIR);

        log.debug("Handling find all technologies - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        // Validate pagination parameters
        if (page < 0 || size <= 0) {
            return buildBadRequestResponse("Invalid pagination parameters: page must be >= 0 and size must be > 0");
        }

        // Validate sort direction
        if (!sortDir.equals("ASC") && !sortDir.equals("DESC")) {
            return buildBadRequestResponse("Invalid sort direction. Use 'ASC' or 'DESC'");
        }

        return servicePort.getAllTechnologies(page, size, sortBy, sortDir)
                .collectList()
                .zipWith(servicePort.countTechnologies())
                .map(tuple -> {
                    List<Technology> technologies = tuple.getT1();
                    Long totalElements = tuple.getT2();
                    List<TechnologyResponse> content = mapper.toResponseList(technologies);
                    return PageResponse.of(content, page, size, totalElements);
                })
                .flatMap(pageResponse -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pageResponse))
                .onErrorResume(this::handleError);
    }


    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable(ID_PATH_VARIABLE));
        log.debug("Handling update technology with id: {}", id);

        return request.bodyToMono(TechnologyRequest.class)
                .flatMap(this::validateRequest)
                .map(req -> mapper.toDomain(id, req))
                .flatMap(req -> servicePort.updateTechnology(id, req))
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnSuccess(r -> log.info("Technology with id {} updated successfully", id))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable(ID_PATH_VARIABLE));
        log.debug("Handling delete technology with id: {}", id);

        return servicePort.deleteTechnology(id)
                .then(ServerResponse.noContent().build())
                .doOnSuccess(r -> log.info("Technology with id {} deleted successfully", id))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> findByIds(ServerRequest request) {
        return request.queryParam(IDS_PARAM)
                .map(idsParam -> {
                    List<Long> ids = Arrays.stream(idsParam.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong)
                            .collect(Collectors.toList());

                    log.debug("Handling find technologies by ids: {}", ids);

                    if (ids.isEmpty()) {
                        return ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(List.of());
                    }

                    return servicePort.getTechnologiesByIds(ids)
                            .collectList()
                            .map(mapper::toResponseList)
                            .flatMap(responses -> ServerResponse
                                    .ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(responses))
                            .onErrorResume(this::handleError);
                })
                .orElse(buildBadRequestResponse("Query parameter 'ids' is required"));
    }

    private Mono<TechnologyRequest> validateRequest(TechnologyRequest request){
        Set<ConstraintViolation<TechnologyRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()){
            String errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            log.warn("Validation failed: {}", errorMessages);
            return Mono.error(new IllegalArgumentException(errorMessages));
        }
        return Mono.just(request);
    }

    private Mono<ServerResponse> handleError(Throwable throwable) {
        log.error("Error handling request: {}", throwable.getMessage());

        if (throwable instanceof TechnologyAlreadyExistsException) {
            return buildErrorResponse(HttpStatus.CONFLICT, throwable.getMessage());
        }

        if (throwable instanceof TechnologyNotFoundException) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, throwable.getMessage());
        }

        if (throwable instanceof IllegalArgumentException) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, throwable.getMessage());
        }

        // Generic server error for unexpected exceptions
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", message);

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorBody);
    }

    private Mono<ServerResponse> buildBadRequestResponse(String message) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }
}
