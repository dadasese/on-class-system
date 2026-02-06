package com.onclass.capacity.usecase;

import com.onclass.capacity.application.dto.request.CapacityRequest;
import com.onclass.capacity.domain.spi.ITechnologyClientPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class CapacityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private ITechnologyClientPort technologyClientPort;

    @Test
    void createCapacity_shouldReturn201_whenValidRequest() {
        // Mock MS-Tecnologia responses
        when(technologyClientPort.existsById(anyLong())).thenReturn(Mono.just(true));

        var request = new CapacityRequest(
                "Backend Development",
                "Core backend programming skills",
                List.of(1L, 2L, 3L)
        );

        webTestClient.post()
                .uri("/api/v1/capabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Backend Development")
                .jsonPath("$.descripcion").isEqualTo("Core backend programming skills");
    }

    @Test
    void createCapacity_shouldReturn400_whenLessThan3Technologies() {
        var request = new CapacityRequest(
                "Invalid",
                "Description",
                List.of(1L, 2L)
        );

        webTestClient.post()
                .uri("/api/v1/capabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createCapacity_shouldReturn400_whenTechnologyNotFound() {
        when(technologyClientPort.existsById(11L)).thenReturn(Mono.just(true));
        when(technologyClientPort.existsById(12L)).thenReturn(Mono.just(true));
        when(technologyClientPort.existsById(999L)).thenReturn(Mono.just(false));

        var request = new CapacityRequest(
                "Invalid Tech",
                "Description",
                List.of(1L, 2L, 999L)
        );

        webTestClient.post()
                .uri("/api/v1/capabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(msg ->
                        ((String) msg).contains("Technology with id 999"));
    }

    @Test
    void findAll_shouldReturnPaginatedResults() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/capabilities")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("sortBy", "name")
                        .queryParam("sortDir", "asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10);
    }
}