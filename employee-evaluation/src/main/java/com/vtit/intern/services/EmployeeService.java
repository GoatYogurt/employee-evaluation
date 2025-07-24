package com.vtit.intern.services;

import com.vtit.intern.models.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getById(Long id);
    Employee create(Employee employee);
    Employee update(Long id, Employee updated);
    void delete(Long id);
}
