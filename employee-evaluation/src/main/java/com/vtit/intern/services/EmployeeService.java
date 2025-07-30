package com.vtit.intern.services;

import com.vtit.intern.dtos.EmployeeDTO;
import com.vtit.intern.dtos.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeDTO getById(Long id);
    EmployeeDTO create(EmployeeDTO employeeDto);
    EmployeeDTO update(Long id, EmployeeDTO employeeDto);
    void delete(Long id);

    PageResponse<EmployeeDTO> getAllEmployees(String name, String username, String email, String department, String position, String role, Double salaryMin, Double salaryMax, Pageable pageable);

    EmployeeDTO patch(Long id, EmployeeDTO employeeDto);
    void changePassword(String username, String oldPassword, String newPassword);
}
