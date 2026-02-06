package com.onclass.capacity.infrastructure.configuration;

import com.onclass.capacity.domain.api.ICapacityServicePort;
import com.onclass.capacity.domain.spi.ICapacityPersistencePort;
import com.onclass.capacity.domain.spi.ITechnologyClientPort;
import com.onclass.capacity.domain.usecase.CapacityUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ICapacityServicePort capacityServicePort(ICapacityPersistencePort persistencePort, ITechnologyClientPort clientPort){
        return new CapacityUseCase(persistencePort, clientPort);
    }

}
