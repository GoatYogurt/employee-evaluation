package com.vtit.intern.dtos.responses;

import com.vtit.intern.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private String name;
    private String username;
    private String email;
    private String position;
    private String department;
    private Double salary;
    private Role role;
}
