package com.onclass.person.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class Person {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private List<Long> bootcampIds;

    public void validate() {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");
        if (name.length() > 100)
            throw new IllegalArgumentException("Name max length is 100");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required");
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Invalid email format");
        if (age == null || age < 16 || age > 120)
            throw new IllegalArgumentException("Age must be between 16 and 120");
    }
}
