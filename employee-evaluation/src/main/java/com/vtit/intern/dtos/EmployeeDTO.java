package com.vtit.intern.dtos;

import com.vtit.intern.models.Employee;
import com.vtit.intern.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String name;
    private String position;
    private String department;
    private double salary;
    private Role role;
}
