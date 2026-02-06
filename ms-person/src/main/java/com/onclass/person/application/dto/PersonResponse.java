package com.onclass.person.application.dto;

import java.util.List;

public record PersonResponse(
        Long id, String name, String email, Integer age,
        List<Long> bootcampIds, int bootcampCount
) {
}
