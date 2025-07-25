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

    public EmployeeController(EmployeeServiceImpl employeeServiceImpl) {
        this.employeeServiceImpl = employeeServiceImpl;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeServiceImpl.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeServiceImpl.getById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestBody EmployeeDTO employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeServiceImpl.create(employeeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDto) {
        return ResponseEntity.ok(employeeServiceImpl.update(id, employeeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
