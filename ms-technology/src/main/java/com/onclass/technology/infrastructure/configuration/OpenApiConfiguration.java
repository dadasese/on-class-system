package com.onclass.technology.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Configures the Swagger UI accessible at /swagger-ui.html
 * 
 * Part of the infrastructure layer - configuration.
 */
@Configuration
public class OpenApiConfiguration {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio Tecnología API")
                        .version("1.0.0")
                        .description("""
                                REST API for managing technologies in the Bootcamp Management System.
                                
                                This microservice provides CRUD operations for technologies that will be 
                                associated with capabilities (skills) in bootcamp programs.
                                
                                ## Features
                                - Create, read, update, and soft-delete technologies
                                - Paginated listing with sorting
                                - Name uniqueness validation
                                - Bulk retrieval by IDs for inter-service communication
                                
                                ## Related User Story
                                **HU-001**: As an administrator, I need to register the technologies 
                                that will be used by the capabilities to know which technologies 
                                the bootcamp is targeting.
                                """)
                        .contact(new Contact()
                                .name("Bootcamp Development Team")
                                .email("dev@bootcamp.com")
                                .url("https://bootcamp.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/api/v1")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://ms-tecnologia:8080/api/v1")
                                .description("Docker Container Server")
                ));
    }
}
