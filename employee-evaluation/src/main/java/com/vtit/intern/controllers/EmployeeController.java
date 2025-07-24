package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EmployeeDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Employee;
import com.vtit.intern.services.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final ModelMapper modelMapper;

    public EmployeeController(EmployeeService employeeService, ModelMapper modelMapper) {
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees().stream().map(employee -> modelMapper.map(employee, EmployeeDTO.class)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);

        EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);
        return ResponseEntity.ok(modelMapper.map(employee, EmployeeDTO.class));
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestBody EmployeeDTO employeeDto) {
        // Convert EmployeeDTO to Employee entity
        Employee employeeToCreate = modelMapper.map(employeeDto, Employee.class);

        // Create the employee using the service
        Employee createdEmployee = employeeService.create(employeeToCreate);

        // Convert the created Employee entity back to EmployeeDTO and return it
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdEmployee, EmployeeDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDto) {
        Employee updatedEmployee = modelMapper.map(employeeDto, Employee.class);
        Employee savedEmployee = employeeService.update(id, updatedEmployee);
        return ResponseEntity.ok(modelMapper.map(savedEmployee, EmployeeDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
