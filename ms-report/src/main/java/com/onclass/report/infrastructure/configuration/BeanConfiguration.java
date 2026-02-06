package com.onclass.report.infrastructure.configuration;

import com.onclass.report.domain.spi.IBootcampDataCollectorPort;
import com.onclass.report.domain.spi.IReportPersistencePort;
import com.onclass.report.domain.usecase.ReportUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public ReportUseCase reportUseCase(IReportPersistencePort persistence,
                                       IBootcampDataCollectorPort dataCollector) {
        return new ReportUseCase(persistence, dataCollector);
    }
}
