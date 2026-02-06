package com.onclass.person.infrastructure.persistence.mapper;

import com.onclass.person.domain.model.Person;
import com.onclass.person.infrastructure.persistence.entity.PersonEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PersonEntityMapper {
    public PersonEntity toEntity(Person d) {
        PersonEntity e = new PersonEntity();
        e.setId(d.getId()); e.setName(d.getName());
        e.setEmail(d.getEmail()); e.setAge(d.getAge());
        return e;
    }
    public Person toDomain(PersonEntity e) {
        return Person.builder()
                .id(e.getId()).name(e.getName())
                .email(e.getEmail()).age(e.getAge())
                .bootcampIds(Collections.emptyList()).build();
    }
}
