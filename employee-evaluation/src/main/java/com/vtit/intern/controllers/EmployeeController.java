package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EmployeeDTO;
import com.vtit.intern.models.Employee;
import com.vtit.intern.services.impl.EmployeeServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeServiceImpl employeeServiceImpl;
    private final ModelMapper modelMapper;

    public EmployeeController(EmployeeServiceImpl employeeServiceImpl, ModelMapper modelMapper) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeServiceImpl.getAllEmployees().stream().map(employee -> modelMapper.map(employee, EmployeeDTO.class)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(modelMapper.map(employeeServiceImpl.getById(id), EmployeeDTO.class));
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestBody EmployeeDTO employeeDto) {
        Employee employeeToCreate = modelMapper.map(employeeDto, Employee.class);
        Employee createdEmployee = employeeServiceImpl.create(employeeToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdEmployee, EmployeeDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDto) {
        Employee updatedEmployee = modelMapper.map(employeeDto, Employee.class);
        Employee savedEmployee = employeeServiceImpl.update(id, updatedEmployee);
        return ResponseEntity.ok(modelMapper.map(savedEmployee, EmployeeDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
