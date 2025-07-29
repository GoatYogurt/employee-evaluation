package com.vtit.intern.dtos;

import com.vtit.intern.models.Employee;
import com.vtit.intern.models.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

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
