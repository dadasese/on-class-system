package com.onclass.capacity.application.router;

import com.onclass.capacity.application.handler.CapacityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class CapacityRouter {

    private static final String BASE_PATH = "/api/v1/capabilities";

    @Bean
    public RouterFunction<ServerResponse> capacityRoutes(CapacityHandler handler) {
        return RouterFunctions.route()
                .path(BASE_PATH, builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::create)
                        .GET("", handler::findAll)
                        .GET("/{id}", handler::findById)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), handler::update)
                        .DELETE("/{id}", handler::delete)
                ).build();
    }
}
