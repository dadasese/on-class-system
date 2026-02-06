package com.onclass.bootcamp.application.router;

import com.onclass.bootcamp.application.handler.BootcampHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class BootcampRouter {

    private static final String BASE = "/api/v1/bootcamps";

    @Bean
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler bootcampHandler){
        return RouterFunctions.route()
                .path(BASE, builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), bootcampHandler::create)
                        .GET("", bootcampHandler::findAll)
                        .GET("/{id}", bootcampHandler::findById)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), bootcampHandler::update)
                        .DELETE("/{id}", bootcampHandler::delete)
                )
                .build();
    }
}
