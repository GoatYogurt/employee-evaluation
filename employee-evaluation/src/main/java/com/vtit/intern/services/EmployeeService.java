package com.vtit.intern.services;

import com.vtit.intern.models.Employee;
import com.vtit.intern.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public Employee getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public Employee update(Long id, Employee updated) {
        return repository.findById(id).map(employee -> {
            employee.setName(updated.getName());
            employee.setPosition(updated.getPosition());
            employee.setDepartment(updated.getDepartment());
            employee.setSalary(updated.getSalary());
            return repository.save(employee);
        }).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
