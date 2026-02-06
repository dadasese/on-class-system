package com.onclass.technology.infrastructure.configuration;

import com.onclass.technology.domain.api.ITechnologyServicePort;
import com.onclass.technology.domain.spi.ITechnologyPersistencePort;
import com.onclass.technology.domain.usecase.TechnologyUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    /**
     * Creates the TechnologyUseCase bean, which implements ITechnologyServicePort.
     * The use case receives the persistence port (adapter) for data access.
     *
     * @param persistencePort the persistence adapter implementing ITechnologyPersistencePort
     * @return the technology service port implementation
     */
    @Bean
    public ITechnologyServicePort technologyServicePort(ITechnologyPersistencePort persistencePort) {
        return new TechnologyUseCase(persistencePort);
    }

}
