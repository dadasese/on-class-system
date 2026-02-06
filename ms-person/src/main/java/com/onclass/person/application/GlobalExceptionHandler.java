package com.onclass.person.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onclass.person.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange ex, Throwable t) {
        HttpStatus s = resolve(t);
        log.error("[{}] {} - {}", s.value(), ex.getRequest().getPath().value(), t.getMessage());

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", s.value(), "error", s.getReasonPhrase(),
                "message", t.getMessage() != null ? t.getMessage() : "Unexpected error",
                "path", ex.getRequest().getPath().value());

        ex.getResponse().setStatusCode(s);
        ex.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = om.writeValueAsBytes(body);
            return ex.getResponse().writeWith(
                    Mono.just(ex.getResponse().bufferFactory().wrap(bytes)));
        } catch (Exception e) { return ex.getResponse().setComplete(); }
    }

    private HttpStatus resolve(Throwable t) {
        if (t instanceof PersonNotFoundException) return HttpStatus.NOT_FOUND;
        if (t instanceof PersonAlreadyExistsException) return HttpStatus.CONFLICT;
        if (t instanceof BootcampNotFoundException) return HttpStatus.BAD_REQUEST;
        if (t instanceof BootcampServiceException) return HttpStatus.SERVICE_UNAVAILABLE;
        if (t instanceof EnrollmentLimitExceededException) return HttpStatus.UNPROCESSABLE_ENTITY;
        if (t instanceof BootcampOverlapException) return HttpStatus.CONFLICT;
        if (t instanceof AlreadyEnrolledException) return HttpStatus.CONFLICT;
        if (t instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}