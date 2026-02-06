package com.onclass.person.application.dto;

import jakarta.validation.constraints.*;

public record PersonRequest (
        @NotBlank(message = "Name is required")
        @Size(max = 100) String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150) String email,
        @NotNull(message = "Age is required")
        @Min(value = 16, message = "Minimum age is 16")
        @Max(value = 120) Integer age
){

}
