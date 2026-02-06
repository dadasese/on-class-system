package com.onclass.person.domain.api;

import com.onclass.person.domain.model.BootcampInfo;
import com.onclass.person.domain.model.Person;
import com.onclass.person.domain.model.PersonReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IPersonServicePort {

    Mono<Person> create(Person person);
    Mono<Person> findById(Long id);
    Flux<Person> findAllPaginated(int page, int size, String sortBy, String sortDir);
    Mono<Person> update(Long id, Person person);
    Mono<Void> delete(Long id);
    Mono<Long> count();
    Mono<Void> enrollInBootcamps(Long personId, List<Long> bootcampIds);
    Flux<BootcampInfo> getEnrolledBootcamps(Long personId);
    Mono<Void> unenrollFromBootcamp(Long personId, Long bootcampId);
    Flux<PersonReport> findByBootcampId(Long bootcampId);

}
