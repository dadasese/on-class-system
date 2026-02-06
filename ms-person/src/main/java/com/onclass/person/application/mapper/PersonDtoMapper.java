package com.onclass.person.application.mapper;

import com.onclass.person.application.dto.BootcampInfoResponse;
import com.onclass.person.application.dto.PersonRequest;
import com.onclass.person.application.dto.PersonResponse;
import com.onclass.person.domain.model.BootcampInfo;
import com.onclass.person.domain.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonDtoMapper {
    public Person toDomain(PersonRequest r) {
        return Person.builder().name(r.name()).email(r.email()).age(r.age()).build();
    }

    public PersonResponse toResponse(Person p) {
        var ids = p.getBootcampIds() != null ? p.getBootcampIds() : java.util.List.<Long>of();
        return new PersonResponse(p.getId(), p.getName(), p.getEmail(),
                p.getAge(), ids, ids.size());
    }

    public BootcampInfoResponse toResponse(BootcampInfo b) {
        return new BootcampInfoResponse(b.id(), b.name(), b.description(),
                b.startDate() != null ? b.startDate().toString() : null,
                b.weeksDuration());
    }
}
