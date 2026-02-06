package com.onclass.bootcamp.application.exception;

import com.onclass.bootcamp.domain.exception.BootcampAlreadyExistsException;
import com.onclass.bootcamp.domain.exception.BootcampNotFoundException;
import com.onclass.bootcamp.domain.exception.CapacityNotFoundException;
import com.onclass.bootcamp.domain.exception.CapacityServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);
        log.error("[{}] {} - {}", status.value(),
                exchange.getRequest().getPath().value(), ex.getMessage());

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error",
                "path", exchange.getRequest().getPath().value()
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof BootcampNotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof BootcampAlreadyExistsException) return HttpStatus.CONFLICT;
        if (ex instanceof CapacityNotFoundException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof CapacityServiceException) return HttpStatus.SERVICE_UNAVAILABLE;
        if (ex instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}