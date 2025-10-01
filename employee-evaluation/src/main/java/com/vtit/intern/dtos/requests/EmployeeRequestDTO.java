package com.vtit.intern.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vtit.intern.enums.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotNull(message = "Staff code is required")
    private Integer staffCode;

    @NotBlank(message = "Username is required")
    @Size(max = 75, message = "Username must not exceed 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Department is required")
    @Size(max = 150, message = "Department must not exceed 150 characters")
    private String department;

    @NotNull(message = "Role is required")
    private String role;

    @NotNull(message = "Level is required")
    private String level;
}
