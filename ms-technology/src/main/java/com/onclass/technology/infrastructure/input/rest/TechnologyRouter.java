package com.onclass.technology.infrastructure.input.rest;

import com.onclass.technology.application.handler.TechnologyHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class TechnologyRouter {

    private static final String BASE_PATH = "/api/v1/technologies";
    private static final String ID_PATH = "/{id}";
    private static final String NAME_PATH = "/name/{name}";
    private static final String IDS_PATH = "/ids";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/technologies",
                    method = RequestMethod.POST,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "create",
                    operation = @Operation(
                            operationId = "createTechnology",
                            summary = "Create a new technology",
                            description = "Creates a new technology with unique name validation",
                            tags = {"Technology"},
                            requestBody = @RequestBody(
                                    description = "Technology to create",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = com.onclass.technology.application.dto.request.TechnologyRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Technology created successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                                    @ApiResponse(responseCode = "409", description = "Technology name already exists")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/technologies",
                    method = RequestMethod.GET,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "findAll",
                    operation = @Operation(
                            operationId = "getAllTechnologies",
                            summary = "Get all technologies",
                            description = "Retrieves all technologies with pagination and sorting",
                            tags = {"Technology"},
                            parameters = {
                                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number (0-indexed)", schema = @Schema(type = "integer", defaultValue = "0")),
                                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "10")),
                                    @Parameter(name = "sortBy", in = ParameterIn.QUERY, description = "Sort field", schema = @Schema(type = "string", defaultValue = "nombre")),
                                    @Parameter(name = "sortDir", in = ParameterIn.QUERY, description = "Sort direction (ASC/DESC)", schema = @Schema(type = "string", defaultValue = "ASC"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/technologies/{id}",
                    method = RequestMethod.GET,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "findById",
                    operation = @Operation(
                            operationId = "getTechnologyById",
                            summary = "Get technology by ID",
                            description = "Retrieves a single technology by its ID",
                            tags = {"Technology"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Technology ID", required = true, schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                                    @ApiResponse(responseCode = "404", description = "Technology not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/technologies/name/{name}",
                    method = RequestMethod.GET,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "findByName",
                    operation = @Operation(
                            operationId = "getTechnologyByName",
                            summary = "Get technology by name",
                            description = "Retrieves a single technology by its name",
                            tags = {"Technology"},
                            parameters = {
                                    @Parameter(name = "name", in = ParameterIn.PATH, description = "Technology name", required = true, schema = @Schema(type = "string"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                                    @ApiResponse(responseCode = "404", description = "Technology not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/technologies/{id}",
                    method = RequestMethod.PUT,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "updateTechnology",
                            summary = "Update a technology",
                            description = "Updates an existing technology",
                            tags = {"Technology"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Technology ID", required = true, schema = @Schema(type = "integer", format = "int64"))
                            },
                            requestBody = @RequestBody(
                                    description = "Updated technology data",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = com.onclass.technology.application.dto.request.TechnologyRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Technology updated successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                                    @ApiResponse(responseCode = "404", description = "Technology not found"),
                                    @ApiResponse(responseCode = "409", description = "Technology name already exists")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/technologies/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteTechnology",
                            summary = "Delete a technology",
                            description = "Soft deletes a technology by its ID",
                            tags = {"Technology"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Technology ID", required = true, schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Technology deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Technology not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/technologies/ids",
                    method = RequestMethod.GET,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "findByIds",
                    operation = @Operation(
                            operationId = "getTechnologiesByIds",
                            summary = "Get technologies by IDs",
                            description = "Retrieves multiple technologies by their IDs (for inter-service communication)",
                            tags = {"Technology"},
                            parameters = {
                                    @Parameter(name = "ids", in = ParameterIn.QUERY, description = "Comma-separated list of technology IDs", required = true, schema = @Schema(type = "string", example = "1,2,3"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                                    @ApiResponse(responseCode = "400", description = "Missing or invalid ids parameter")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> technologyRoutes(TechnologyHandler handler) {
        return RouterFunctions.route()
                .path(BASE_PATH, builder -> builder
                        // Create technology - POST /technologies
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::create)

                        // Get all technologies (paginated) - GET /technologies
                        .GET("", handler::findAll)

                        // Get technologies by IDs - GET /technologies/ids?ids=1,2,3
                        // Note: Must be before /{id} to avoid conflict
                        .GET(IDS_PATH, handler::findByIds)

                        // Get technology by name - GET /technologies/name/{name}
                        // Note: Must be before /{id} to avoid conflict
                        .GET(NAME_PATH, handler::findByName)

                        // Get technology by ID - GET /technologies/{id}
                        .GET(ID_PATH, handler::findById)

                        // Update technology - PUT /technologies/{id}
                        .PUT(ID_PATH, accept(MediaType.APPLICATION_JSON), handler::update)

                        // Delete technology - DELETE /technologies/{id}
                        .DELETE(ID_PATH, handler::delete)
                )
                .build();
    }
}
