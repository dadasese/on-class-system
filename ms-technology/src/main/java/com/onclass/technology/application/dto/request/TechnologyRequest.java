package com.onclass.technology.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TechnologyRequest {
    @NotBlank(message = "Technology name is required")
    @Size(max = 50, message = "Technology name must not exceed 50 characters")
    private String name;
    @NotBlank(message = "Technology description is required")
    @Size(max = 90, message = "Technology description must not exceed 90 characters")
    private String description;
}
