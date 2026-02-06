package com.onclass.technology.domain.usecase;

import com.onclass.technology.domain.exception.InvalidTechnologyException;
import com.onclass.technology.domain.exception.TechnologyAlreadyExistsException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.spi.ITechnologyPersistencePort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyUseCaseTest {

    @Mock
    private ITechnologyPersistencePort persistencePort;

    @InjectMocks
    private TechnologyUseCase useCase;

    @Test
    @DisplayName("Should create technology when all fields are valid")
    void create_success() {
        // Arrange
        Technology input = new Technology(null, "Java", "Programming language");
        Technology saved = new Technology(1L, "Java", "Programming language");

        when(persistencePort.existByName("Java")).thenReturn(Mono.just(false));
        when(persistencePort.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.createTechnology(input))
                .expectNextMatches(t -> t.getId().equals(1L) && t.getName().equals("Java"))
                .verifyComplete();

        verify(persistencePort).save(any());
    }

    @Test
    @DisplayName("Should fail when name is null")
    void create_failWhenNameNull() {
        Technology input = new Technology(null, null, "Valid description");

        StepVerifier.create(useCase.createTechnology(input))
                .expectError(InvalidTechnologyException.class)
                .verify();

        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should fail when name is empty")
    void create_failWhenNameEmpty() {
        Technology input = new Technology(null, "", "Valid description");

        StepVerifier.create(useCase.createTechnology(input))
                .expectError(InvalidTechnologyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail when name exceeds 50 characters")
    void create_failWhenNameTooLong() {
        String longName = "A".repeat(51);
        Technology input = new Technology(null, longName, "Valid description");

        StepVerifier.create(useCase.createTechnology(input))
                .expectError(InvalidTechnologyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should succeed when name is exactly 50 characters")
    void create_successWhenNameAtMaxLength() {
        String maxName = "A".repeat(50);
        Technology input = new Technology(null, maxName, "Valid description");
        Technology saved = new Technology(1L, maxName, "Valid description");

        when(persistencePort.existByName(maxName)).thenReturn(Mono.just(false));
        when(persistencePort.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.createTechnology(input))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when description is null")
    void create_failWhenDescriptionNull() {
        Technology input = new Technology(null, "Java", null);

        StepVerifier.create(useCase.createTechnology(input))
                .expectError(InvalidTechnologyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail when description is empty")
    void create_failWhenDescriptionEmpty() {
        Technology input = new Technology(null, "Java", "");

        StepVerifier.create(useCase.createTechnology(input))
                .expectError(InvalidTechnologyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail when description exceeds 90 characters")
    void create_failWhenDescriptionTooLong() {
        String longDesc = "z".repeat(91);
        Technology input = new Technology(null, "Java", longDesc);

        StepVerifier.create(useCase.createTechnology(input))
                .expectError(InvalidTechnologyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should succeed when description is exactly 90 characters")
    void create_successWhenDescriptionAtMaxLength() {
        String maxDesc = "B".repeat(90);
        Technology input = new Technology(null, "Java", maxDesc);
        Technology saved = new Technology(1L, "Java", maxDesc);

        when(persistencePort.existByName("Java")).thenReturn(Mono.just(false));
        when(persistencePort.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.createTechnology(input))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when technology name already exists")
    void create_failWhenNameAlreadyExists() {
        Technology input = new Technology(null, "Java", "Programming language");

        when(persistencePort.existByName("Java")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createTechnology(input))
                .expectError(TechnologyAlreadyExistsException.class)
                .verify();

        verify(persistencePort, never()).save(any());
    }
}