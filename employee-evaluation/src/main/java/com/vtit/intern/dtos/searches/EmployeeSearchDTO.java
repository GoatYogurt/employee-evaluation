package com.vtit.intern.dtos.searches;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearchDTO {
    private String name;
    private String username;
    private String email;
    private String department;
    private String position;
    private String role;
}
