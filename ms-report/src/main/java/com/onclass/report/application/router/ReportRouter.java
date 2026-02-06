package com.onclass.report.application.router;

import com.onclass.report.application.handler.ReportHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ReportRouter {

    @Bean
    public RouterFunction<ServerResponse> reportRoutes(ReportHandler h) {
        return RouterFunctions.route()
                .path("/api/v1/reports", b -> b
                        .POST("/events/bootcamp-created", accept(MediaType.APPLICATION_JSON),
                                h::handleBootcampEvent)
                        .POST("/events/bootcamp-updated", accept(MediaType.APPLICATION_JSON),
                                h::handleBootcampUpdated)
                        .POST("/generate/{bootcampId}", h::generateReport)
                        .GET("/most-popular", h::findMostPopular)
                        .GET("", h::findAll)
                        .GET("/bootcamp/{bootcampId}", h::findByBootcampId)
                )
                .build();
    }
}
