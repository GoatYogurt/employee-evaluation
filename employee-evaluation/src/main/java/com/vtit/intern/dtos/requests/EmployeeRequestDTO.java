package com.vtit.intern.dtos.requests;

import com.vtit.intern.models.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(max = 75, message = "Username must not exceed 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Position is required")
    @Size(max = 75, message = "Position must not exceed 75 characters")
    private String position;

    @NotBlank(message = "Department is required")
    @Size(max = 75, message = "Department must not exceed 75 characters")
    private String department;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private Double salary;

    @NotNull(message = "Role is required")
    private Role role;
}
