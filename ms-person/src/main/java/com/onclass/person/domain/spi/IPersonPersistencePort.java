package com.onclass.person.domain.spi;

import com.onclass.person.domain.model.Person;
import com.onclass.person.domain.model.PersonReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IPersonPersistencePort {
    Mono<Person> save(Person person);
    Mono<Person> findById(Long id);
    Mono<Person> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Flux<Person> findAllPaginated(int page, int size, String sortBy, String sortDir);
    Mono<Long> count();
    Mono<Void> deleteById(Long id);
    Mono<Void> enrollInBootcamp(Long personId, Long bootcampId);
    Mono<Void> enrollInBootcamps(Long personId, List<Long> bootcampIds);
    Flux<Long> findBootcampIdsByPersonId(Long personId);
    Mono<Integer> countBootcampsByPersonId(Long personId);
    Mono<Void> unenrollFromBootcamp(Long personId, Long bootcampId);
    Flux<PersonReport> findByBootcampId(Long bootcampId);

}
