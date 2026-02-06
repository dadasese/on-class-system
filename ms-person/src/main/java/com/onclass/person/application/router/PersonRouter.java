package com.onclass.person.application.router;

import com.onclass.person.application.handler.PersonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
public class PersonRouter {
    private static final String BASE = "/api/v1/persons";

    @Bean
    public RouterFunction<ServerResponse> personRoutes(PersonHandler h) {
        return RouterFunctions.route()
                .path(BASE, b -> b
                        .POST("", accept(MediaType.APPLICATION_JSON), h::create)
                        .GET("", h::findAll)
                        .GET("/{id}", h::findById)
                        .GET("/by-bootcamp/{bootcampId}", h::findByBootcamp)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), h::update)
                        .DELETE("/{id}", h::delete)
                        .POST("/{id}/enrollments", accept(MediaType.APPLICATION_JSON), h::enroll)
                        .GET("/{id}/enrollments", h::getEnrollments)
                        .DELETE("/{id}/enrollments/{bootcampId}", h::unenroll)
                )
                .build();
    }
}
