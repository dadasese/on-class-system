package com.onclass.person.domain.usecase;

import com.onclass.person.domain.api.IPersonServicePort;
import com.onclass.person.domain.model.BootcampInfo;
import com.onclass.person.domain.model.Person;
import com.onclass.person.domain.exception.*;
import com.onclass.person.domain.model.PersonReport;
import com.onclass.person.domain.spi.IBootcampClientPort;
import com.onclass.person.domain.spi.IPersonPersistencePort;
import com.onclass.person.infrastructure.client.ReportEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class PersonUseCase implements IPersonServicePort {
    
    private static final Logger log = LoggerFactory.getLogger(PersonUseCase.class);
    private static final int MAX_BOOTCAMPS = 5;

    private final IPersonPersistencePort persistence;
    private final IBootcampClientPort bootcampClient;
    private final ReportEventPublisher reportEventPublisher;

    public PersonUseCase(IPersonPersistencePort persistence,
                         IBootcampClientPort bootcampClient, ReportEventPublisher reportEventPublisher) {
        this.persistence = persistence;
        this.bootcampClient = bootcampClient;
        this.reportEventPublisher = reportEventPublisher;
    }


    @Override
    public Mono<Person> create(Person person) {
        person.validate();
        return persistence.existsByEmail(person.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new PersonAlreadyExistsException(person.getEmail()));
                    }
                    return persistence.save(person);
                })
                .flatMap(this::enrichWithBootcampIds);
    }
    
    @Override
    public Mono<Person> findById(Long id) {
        return persistence.findById(id)
                .switchIfEmpty(Mono.error(new PersonNotFoundException(id)))
                .flatMap(this::enrichWithBootcampIds);
    }

    @Override
    public Flux<Person> findAllPaginated(int page, int size, String sortBy, String sortDir) {
        return persistence.findAllPaginated(page, size, sortBy, sortDir)
                .flatMap(this::enrichWithBootcampIds);
    }

    @Override
    public Mono<Person> update(Long id, Person person) {
        person.validate();
        return persistence.findById(id)
                .switchIfEmpty(Mono.error(new PersonNotFoundException(id)))
                .flatMap(existing -> {
                    if (!existing.getEmail().equals(person.getEmail())) {
                        return persistence.existsByEmail(person.getEmail())
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(
                                                new PersonAlreadyExistsException(person.getEmail()));
                                    }
                                    return Mono.just(existing);
                                });
                    }
                    return Mono.just(existing);
                })
                .flatMap(existing -> {
                    Person updated = person.builder()
                            .id(id).name(person.getName())
                            .email(person.getEmail()).age(person.getAge())
                            .build();
                    return persistence.save(updated);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return persistence.findById(id)
                .switchIfEmpty(Mono.error(new PersonNotFoundException(id)))
                .flatMap(p -> persistence.deleteById(id));
    }
    @Override
    public Mono<Long> count() {
        return persistence.count();
    }

    @Override
    public Mono<Void> enrollInBootcamps(Long personId, List<Long> bootcampIds) {
        if (bootcampIds == null || bootcampIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException("At least one bootcamp required"));
        }
        if (bootcampIds.size() > MAX_BOOTCAMPS) {
            return Mono.error(new IllegalArgumentException(
                    "Cannot enroll in more than " + MAX_BOOTCAMPS + " bootcamps at once"));
        }

        return persistence.findById(personId)
                .switchIfEmpty(Mono.error(new PersonNotFoundException(personId)))
                .then(
                        persistence.findBootcampIdsByPersonId(personId)
                                .collectList()
                )
                .flatMap(currentIds -> {
                    List<Long> trulyNew = bootcampIds.stream()
                            .distinct()
                            .filter(id -> !currentIds.contains(id))
                            .toList();

                    if (trulyNew.isEmpty()) {
                        log.info("Person {} already enrolled in all requested bootcamps", personId);
                        return Mono.empty();
                    }

                    int totalAfter = currentIds.size() + trulyNew.size();
                    if (totalAfter > MAX_BOOTCAMPS) {
                        return Mono.error(new EnrollmentLimitExceededException(
                                currentIds.size(), trulyNew.size()));
                    }

                    List<Long> allIds = new ArrayList<>(currentIds);
                    allIds.addAll(trulyNew);

                    return bootcampClient.findByIds(allIds)
                            .collectList()
                            .flatMap(allBootcamps -> {
                                // Verify all new bootcamps were found
                                List<Long> foundIds = allBootcamps.stream()
                                        .map(BootcampInfo::id).toList();
                                for (Long newId : trulyNew) {
                                    if (!foundIds.contains(newId)) {
                                        return Mono.error(new BootcampNotFoundException(newId));
                                    }
                                }

                                return validateNoOverlaps(allBootcamps)
                                        .then(persistence.enrollInBootcamps(personId, trulyNew))
                                        .doOnSuccess(v -> {
                                            reportEventPublisher.publishEnrollmentCompleted(trulyNew);
                                        });
                            });
                })
                .doOnSuccess(v -> log.info("Person {} enrolled in bootcamps {}",
                        personId, bootcampIds));
    }

    private Mono<Void> validateNoOverlaps(List<BootcampInfo> bootcamps) {
        for (int i = 0; i < bootcamps.size(); i++) {
            for (int j = i + 1; j < bootcamps.size(); j++) {
                BootcampInfo a = bootcamps.get(i);
                BootcampInfo b = bootcamps.get(j);
                if (a.overlapsWith(b)) {
                    return Mono.error(new BootcampOverlapException(
                            a.name(), b.name()));
                }
            }
        }
        return Mono.empty();
    }


    @Override
    public Flux<BootcampInfo> getEnrolledBootcamps(Long personId) {
        return persistence.findById(personId)
                .switchIfEmpty(Mono.error(new PersonNotFoundException(personId)))
                .thenMany(persistence.findBootcampIdsByPersonId(personId)
                        .collectList()
                        .flatMapMany(ids -> {
                            if (ids.isEmpty()) return Flux.empty();
                            return bootcampClient.findByIds(ids);
                        }));
    }

    @Override
    public Mono<Void> unenrollFromBootcamp(Long personId, Long bootcampId) {
        return persistence.findById(personId)
                .switchIfEmpty(Mono.error(new PersonNotFoundException(personId)))
                .then(persistence.unenrollFromBootcamp(personId, bootcampId))
                .doOnSuccess(v -> log.info("Person {} unenrolled from bootcamp {}",
                        personId, bootcampId));
    }

    private Mono<Person> enrichWithBootcampIds(Person person) {
        return persistence.findBootcampIdsByPersonId(person.getId())
                .collectList()
                .map(ids -> Person.builder()
                        .id(person.getId()).name(person.getName())
                        .email(person.getEmail()).age(person.getAge())
                        .bootcampIds(ids)
                        .build());
    }

    @Override
    public Flux<PersonReport> findByBootcampId(Long bootcampId) {
        return persistence.findByBootcampId(bootcampId);
    }
}
