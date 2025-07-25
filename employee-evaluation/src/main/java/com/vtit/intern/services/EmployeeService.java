package com.vtit.intern.services;

import com.vtit.intern.dtos.EmployeeDTO;
import com.vtit.intern.models.Employee;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getById(Long id);
    EmployeeDTO create(EmployeeDTO employeeDto);
    EmployeeDTO update(Long id, EmployeeDTO employeeDto);
    void delete(Long id);
}
