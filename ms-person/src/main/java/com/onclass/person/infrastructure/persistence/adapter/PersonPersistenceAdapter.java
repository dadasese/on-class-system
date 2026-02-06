package com.onclass.person.infrastructure.persistence.adapter;

import com.onclass.person.domain.model.Person;
import com.onclass.person.domain.model.PersonReport;
import com.onclass.person.domain.spi.IPersonPersistencePort;
import com.onclass.person.infrastructure.persistence.entity.PersonBootcampEntity;
import com.onclass.person.infrastructure.persistence.entity.PersonEntity;
import com.onclass.person.infrastructure.persistence.mapper.PersonEntityMapper;
import com.onclass.person.infrastructure.persistence.repository.IPersonBootcampRepository;
import com.onclass.person.infrastructure.persistence.repository.IPersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class PersonPersistenceAdapter implements IPersonPersistencePort {

    private final IPersonRepository repo;
    private final IPersonBootcampRepository pbRepo;
    private final PersonEntityMapper mapper;
    private final DatabaseClient databaseClient;


    @Override public Mono<Person> save(Person p) {
        return repo.save(mapper.toEntity(p)).map(mapper::toDomain);
    }
    @Override public Mono<Person> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }
    @Override public Mono<Person> findByEmail(String c) {
        return repo.findByEmail(c).map(mapper::toDomain);
    }
    @Override public Mono<Boolean> existsByEmail(String c) {
        return repo.existsByEmail(c);
    }
    @Override public Flux<Person> findAllPaginated(int page, int size,
                                                    String sortBy, String sortDir) {
        int offset = page * size;
        boolean asc = "asc".equalsIgnoreCase(sortDir);
        Flux<PersonEntity> r;
        if ("age".equalsIgnoreCase(sortBy)) {
            r = asc ? repo.findAllByAgeAsc(offset, size) : repo.findAllByAgeDesc(offset, size);
        } else {
            r = asc ? repo.findAllByNameAsc(offset, size) : repo.findAllByNameDesc(offset, size);
        }
        return r.map(mapper::toDomain);
    }
    @Override public Mono<Long> count() { return repo.countActive(); }
    @Override public Mono<Void> deleteById(Long id) {
        return repo.findById(id).flatMap(repo::delete).then();
    }

    // ── Enrollment ──
    @Override public Mono<Void> enrollInBootcamp(Long personId, Long bootcampId) {
        return pbRepo.save(new PersonBootcampEntity(personId, bootcampId)).then();
    }
    @Override
    @Transactional
    public Mono<Void> enrollInBootcamps(Long personId, List<Long> bootcampIds) {
        return Flux.fromIterable(bootcampIds)
                .map(bId -> new PersonBootcampEntity(personId, bId))
                .flatMap(pbRepo::save)
                .then();
    }
    @Override public Flux<Long> findBootcampIdsByPersonId(Long personId) {
        return pbRepo.findBootcampIdsByPersonId(personId);
    }
    @Override public Mono<Integer> countBootcampsByPersonId(Long personId) {
        return pbRepo.countByPersonId(personId);
    }
    @Override public Mono<Void> unenrollFromBootcamp(Long personId, Long bootcampId) {
        return pbRepo.deleteByPersonIdAndBootcampId(personId, bootcampId);
    }
    @Override
    public Flux<PersonReport> findByBootcampId(Long bootcampId) {
        String sql = """
        SELECT p.id, p.name, p.email
        FROM persons p
        INNER JOIN person_bootcamps pb ON p.id = pb.person_id
        WHERE pb.bootcamp_id = :bootcampId
        """;

        return databaseClient.sql(sql)
                .bind("bootcampId", bootcampId)
                .map((row, meta) -> new PersonReport(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("email", String.class)
                ))
                .all();
    }
}