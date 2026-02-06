package com.onclass.technology.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class TechnologyResponse {
    private Long id;
    private String name;
    private String description;
}
