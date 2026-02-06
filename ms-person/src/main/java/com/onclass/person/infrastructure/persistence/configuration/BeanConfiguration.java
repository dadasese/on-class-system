package com.onclass.person.infrastructure.persistence.configuration;

import com.onclass.person.domain.api.IPersonServicePort;
import com.onclass.person.domain.spi.IBootcampClientPort;
import com.onclass.person.domain.spi.IPersonPersistencePort;
import com.onclass.person.domain.usecase.PersonUseCase;
import com.onclass.person.infrastructure.client.ReportEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IPersonServicePort personServicePort(IPersonPersistencePort persistencePort, IBootcampClientPort clientPort, ReportEventPublisher reportEventPublisher){
        return new PersonUseCase(persistencePort, clientPort, reportEventPublisher);
    }

}
