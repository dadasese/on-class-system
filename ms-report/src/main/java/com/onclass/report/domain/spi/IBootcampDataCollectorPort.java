package com.onclass.report.domain.spi;

import com.onclass.report.domain.model.BootcampExternalData;
import com.onclass.report.domain.model.PersonSnapshot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBootcampDataCollectorPort {

    Mono<BootcampExternalData> fetchBootcampData(Long bootcampId);
    Flux<PersonSnapshot> fetchEnrolledPersons(Long bootcampId);
}
