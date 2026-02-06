 On-Class System

## Project Overview

Java 21 microservices monorepo for an online education platform. Built with Spring Boot 3.4.1, Spring WebFlux (reactive), and Gradle 9.3.0. All services use reactive/non-blocking programming with Project Reactor (`Mono`/`Flux`).

## Repository Structure

```
on-class-system/
├── api-gateway/       # Spring Cloud Gateway (port 8080) - routes to all services
├── ms-technology/     # Technology management (port 8081) - MySQL/R2DBC
├── ms-capacity/       # Capacity management (port 8082) - MySQL/R2DBC
├── ms-bootcamp/       # Bootcamp management (port 8083) - MySQL/R2DBC, Resilience4j
├── ms-person/         # Person/user management (port 8084) - MySQL/R2DBC
└── ms-report/         # Reporting service (port 8085) - MongoDB reactive
```

Each microservice is an independent Gradle project with its own `build.gradle`, `settings.gradle`, and `gradlew` wrapper.

## Architecture

All microservices follow **Hexagonal Architecture (Clean Architecture)** with three layers:

```
com.onclass.<service>/
├── domain/
│   ├── model/          # Domain entities (POJOs, no framework annotations)
│   ├── usecase/        # Business logic implementations
│   ├── ports/
│   │   ├── api/        # Inbound ports (service interfaces, e.g. ITechnologyServicePort)
│   │   └── spi/        # Outbound ports (persistence interfaces, e.g. ITechnologyPersistencePort)
│   └── exception/      # Domain-specific exceptions
├── application/
│   ├── dto/            # Request/response DTOs for the API
│   ├── handler/        # WebFlux request handlers (equivalent to controllers)
│   ├── router/         # Functional route definitions (RouterFunction<ServerResponse>)
│   ├── mapper/         # DTO <-> domain model mappers
│   └── GlobalExceptionHandler.java
└── infrastructure/
    ├── persistence/    # Repository implementations, DB entities, adapters
    ├── client/         # WebClient adapters for inter-service calls
    └── configuration/  # Spring beans, R2DBC config, WebClient config
```

**Key patterns:**
- Functional routing (no `@RestController`) - routes defined in `*Router.java` classes
- Port/Adapter pattern - domain layer has zero framework dependencies
- All database access is reactive (R2DBC for MySQL, reactive MongoDB driver)
- Inter-service communication via WebClient (non-blocking HTTP)
- Resilience4j in ms-bootcamp (circuit breaker, retry, timeout, bulkhead)

## Build & Run Commands

Each service must be built from its own directory using the Gradle wrapper:

```bash
# Build a specific service
cd ms-technology && ./gradlew build

# Run tests for a specific service
cd ms-technology && ./gradlew test

# Run tests with JaCoCo coverage verification
cd ms-technology && ./gradlew testWithCoverage

# Build the bootable JAR
cd ms-technology && ./gradlew bootJar
```

There is **no root-level Gradle build** - each service is independent.

## Testing

- **Framework**: JUnit 5 + Mockito + Reactor Test (`StepVerifier`)
- **Integration tests**: H2 in-memory database via R2DBC (MySQL services), embedded MongoDB (ms-report)
- **API Gateway tests**: Wiremock for downstream service mocking
- **Coverage**: JaCoCo with 70% minimum overall, 80% minimum for `domain.usecase.*` classes
- **Coverage exclusions**: DTOs, configuration classes, exception classes, and main application classes

Run all tests for a service:
```bash
cd <service-dir> && ./gradlew test
```

Coverage reports are generated at `<service-dir>/build/reports/jacoco/test/html/`.

## Code Conventions

- **Java 21** - use modern Java features where appropriate
- **Lombok** - used extensively (`@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Getter`, `@Setter`)
- **Reactive types** - all service/repository methods return `Mono<T>` or `Flux<T>`, never block
- **Port interfaces** - prefixed with `I` (e.g., `ITechnologyServicePort`, `ITechnologyPersistencePort`)
- **Package structure** - strict layering: domain has no imports from application or infrastructure
- **DTOs** - separate request and response DTOs in `application/dto/`
- **Exception handling** - centralized via `GlobalExceptionHandler` using `@ControllerAdvice`
- **Validation** - Spring Validation annotations on DTOs (`@NotBlank`, `@Size`, etc.)
- **API documentation** - SpringDoc OpenAPI (Swagger UI at `/swagger-ui.html` on each service)
- **No explicit code formatter** configured - follow standard Java conventions

## API Gateway Routing

The gateway (port 8080) routes requests to backend services:

| Path Pattern                   | Target Service     |
|-------------------------------|--------------------|
| `/api/v1/technologies/**`     | ms-technology:8081 |
| `/api/v1/capabilities/**`     | ms-capacity:8082   |
| `/api/v1/bootcamps/**`        | ms-bootcamp:8083   |
| `/api/v1/persons/**`          | ms-person:8084     |
| `/api/v1/reports/**`          | ms-report:8085     |

CORS is configured for `localhost:4200` (Angular) and `localhost:3000` (React).

## Key Dependencies

| Dependency | Version | Used In |
|-----------|---------|---------|
| Spring Boot | 3.4.1 | All services (3.3.5 for gateway) |
| Spring Cloud | 2023.0.3 | api-gateway |
| Resilience4j | 2.2.0 | ms-bootcamp |
| R2DBC MySQL | 1.3.0 | ms-technology, ms-capacity, ms-bootcamp, ms-person |
| SpringDoc OpenAPI | 2.7.0 | All services except gateway |
| Lombok | managed | All services |
| JaCoCo | 0.8.11 | All services except gateway |

## Database Configuration

- **MySQL services** (ms-technology, ms-capacity, ms-bootcamp, ms-person): R2DBC with connection pooling. Each service has its own database (`db_technology`, `db_capacity`, `db_bootcamp`, `db_person`).
- **MongoDB service** (ms-report): Reactive MongoDB driver, database `db_report`.
- **Test databases**: H2 in-memory (MySQL services), embedded MongoDB (ms-report).

## Configuration Profiles

Each service supports multiple Spring profiles defined in `application.yaml`:
- **default** - local development (localhost databases)
- **docker** - containerized deployment (service hostnames)
- **test** - test configuration (in-memory databases)

## Important Notes

- Never add `spring-boot-starter-web` to any service - all services use WebFlux (`spring-boot-starter-webflux`)
- The domain layer must remain framework-independent - no Spring annotations in `domain/` packages
- All inter-service calls should be non-blocking using `WebClient`
- When adding new endpoints, use functional routing (`RouterFunction`) not annotation-based controllers
- ms-report uses Spring Security with basic authentication
