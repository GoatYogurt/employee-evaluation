package com.vtit.intern.dtos.responses;

import com.vtit.intern.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private String fullName;
    private String username;
    private String email;
    private String department;
    private Role role;
}
