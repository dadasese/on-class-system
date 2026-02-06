package com.onclass.bootcamp.infrastructure.configuration;


import com.onclass.bootcamp.domain.saga.SagaOrchestrator;
import com.onclass.bootcamp.domain.saga.steps.DeleteBootcampStep;
import com.onclass.bootcamp.domain.saga.steps.DeleteCapacityRelationsStep;
import com.onclass.bootcamp.domain.saga.steps.DeleteOrphanedCapacitiesStep;
import com.onclass.bootcamp.domain.saga.steps.SaveBootcampSnapshotStep;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.ICapacityClientPort;
import com.onclass.bootcamp.domain.usecase.BootcampUseCase;
import com.onclass.bootcamp.infrastructure.client.ReportEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@Configuration
@EnableR2dbcAuditing
public class BeanConfiguration {

    @Bean
    public BootcampUseCase bootcampUseCase(IBootcampPersistencePort persistencePort,
                                           ICapacityClientPort capacityClientPort,
                                           ReportEventPublisher reportEventPublisher,
                                           SagaOrchestrator sagaOrchestrator,
                                           SaveBootcampSnapshotStep SaveBootcampSnapshotStep,
                                           DeleteCapacityRelationsStep deleteRelationsStep,
                                           DeleteBootcampStep deleteBootcampStep,
                                           DeleteOrphanedCapacitiesStep deleteCapacitiesStep){
        return new BootcampUseCase(persistencePort,
                capacityClientPort,
                reportEventPublisher,
                sagaOrchestrator,
                SaveBootcampSnapshotStep,
                deleteRelationsStep,
                deleteBootcampStep,
                deleteCapacitiesStep);
    }

}
