package com.onclass.person.application.handler;

import com.onclass.person.application.dto.EnrollRequest;
import com.onclass.person.application.dto.PageResponse;
import com.onclass.person.application.dto.PersonRequest;
import com.onclass.person.application.mapper.PersonDtoMapper;
import com.onclass.person.domain.api.IPersonServicePort;
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
public class PersonHandler {
    private final IPersonServicePort service;
    private final PersonDtoMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(PersonRequest.class)
                .flatMap(this::validate).map(mapper::toDomain)
                .flatMap(service::create).map(mapper::toResponse)
                .flatMap(r -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(r));
    }

    public Mono<ServerResponse> findById(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return service.findById(id).map(mapper::toResponse)
                .flatMap(r -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(r));
    }

    public Mono<ServerResponse> findAll(ServerRequest req) {
        int page = Integer.parseInt(req.queryParam("page").orElse("0"));
        int size = Integer.parseInt(req.queryParam("size").orElse("10"));
        String sortBy = req.queryParam("sortBy").orElse("name");
        String sortDir = req.queryParam("sortDir").orElse("asc");

        return service.findAllPaginated(page, size, sortBy, sortDir)
                .map(mapper::toResponse).collectList()
                .zipWith(service.count())
                .flatMap(t -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new PageResponse<>(t.getT1(), page, size, t.getT2(),
                                (int) Math.ceil((double) t.getT2() / size))));
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(PersonRequest.class)
                .flatMap(this::validate).map(mapper::toDomain)
                .flatMap(p -> service.update(id, p)).map(mapper::toResponse)
                .flatMap(r -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(r));
    }
    public Mono<ServerResponse> delete(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return service.delete(id).then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> enroll(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(EnrollRequest.class)
                .flatMap(this::validate)
                .flatMap(r -> service.enrollInBootcamps(id, r.bootcampIds()))
                .then(ServerResponse.ok().bodyValue(
                        java.util.Map.of("message", "Enrollment successful")));
    }
    public Mono<ServerResponse> getEnrollments(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return service.getEnrolledBootcamps(id)
                .map(mapper::toResponse).collectList()
                .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(list));
    }

    public Mono<ServerResponse> unenroll(ServerRequest req) {
        Long personId = Long.parseLong(req.pathVariable("id"));
        Long bootcampId = Long.parseLong(req.pathVariable("bootcampId"));
        return service.unenrollFromBootcamp(personId, bootcampId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> findByBootcamp(ServerRequest req) {
        Long bootcampId = Long.parseLong(req.pathVariable("bootcampId"));
        return service.findByBootcampId(bootcampId)
                .collectList()
                .flatMap(persons -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(persons));
    }

    private <T> Mono<T> validate(T r) {
        var v = validator.validate(r);
        if (!v.isEmpty()) {
            String msg = v.stream().map(x -> x.getPropertyPath() + ": " + x.getMessage())
                    .reduce((a,b) -> a + "; " + b).orElse("Validation failed");
            return Mono.error(new IllegalArgumentException(msg));
        }
        return Mono.just(r);
    }

}
