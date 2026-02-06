package com.onclass.technology.infrastructure.input.rest;

import com.onclass.technology.domain.exception.TechnologyAlreadyExistsException;
import com.onclass.technology.domain.exception.TechnologyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


        @ExceptionHandler(TechnologyNotFoundException.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleTechnologyNotFound(TechnologyNotFoundException ex) {
            log.warn("Technology not found: {}", ex.getMessage());

            return Mono.just(buildErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage(),
                    "/api/v1/technologies"
            ));
        }

        @ExceptionHandler(TechnologyAlreadyExistsException.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleTechnologyAlreadyExists(TechnologyAlreadyExistsException ex) {
            log.warn("Technology already exists: {}", ex.getMessage());

            return Mono.just(buildErrorResponse(
                    HttpStatus.CONFLICT,
                    ex.getMessage(),
                    "/api/v1/technologies"
            ));
        }


        @ExceptionHandler(WebExchangeBindException.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleValidationErrors(WebExchangeBindException ex) {
            log.warn("Validation error: {}", ex.getMessage());

            String errors = ex.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            return Mono.just(buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed: " + errors,
                    "/api/v1/technologies"
            ));
        }

        @ExceptionHandler(ServerWebInputException.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleServerWebInputException(ServerWebInputException ex) {
            log.warn("Invalid input: {}", ex.getMessage());

            return Mono.just(buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request body",
                    "/api/v1/technologies"
            ));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgument(IllegalArgumentException ex) {
            log.warn("Illegal argument: {}", ex.getMessage());

            return Mono.just(buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage(),
                    "/api/v1/technologies"
            ));
        }

        @ExceptionHandler(NumberFormatException.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleNumberFormatException(NumberFormatException ex) {
            log.warn("Invalid number format: {}", ex.getMessage());

            return Mono.just(buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Invalid ID format. Expected a numeric value.",
                    "/api/v1/technologies"
            ));
        }

        @ExceptionHandler(Exception.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
            log.error("Unexpected error occurred", ex);

            return Mono.just(buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred. Please try again later.",
                    "/api/v1/technologies"
            ));
        }

        private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, String path) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", LocalDateTime.now().toString());
            errorBody.put("status", status.value());
            errorBody.put("error", status.getReasonPhrase());
            errorBody.put("message", message);
            errorBody.put("path", path);

            return ResponseEntity.status(status).body(errorBody);
        }
}
