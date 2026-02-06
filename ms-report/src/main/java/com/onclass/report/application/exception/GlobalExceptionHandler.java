package com.onclass.report.application.exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onclass.report.domain.exception.BootcampDataException;
import com.onclass.report.domain.exception.PersonDataException;
import com.onclass.report.domain.exception.ReportNotFoundException;
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
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange ex, Throwable t) {
        HttpStatus s = resolve(t);
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", s.value(), "error", s.getReasonPhrase(),
                "message", t.getMessage() != null ? t.getMessage() : "Unexpected error",
                "path", ex.getRequest().getPath().value());
        ex.getResponse().setStatusCode(s);
        ex.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            return ex.getResponse().writeWith(Mono.just(
                    ex.getResponse().bufferFactory().wrap(om.writeValueAsBytes(body))));
        } catch (Exception e) { return ex.getResponse().setComplete(); }
    }

    private HttpStatus resolve(Throwable t) {
        if (t instanceof ReportNotFoundException) return HttpStatus.NOT_FOUND;
        if (t instanceof BootcampDataException) return HttpStatus.BAD_GATEWAY;
        if (t instanceof PersonDataException) return HttpStatus.BAD_GATEWAY;
        if (t instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}