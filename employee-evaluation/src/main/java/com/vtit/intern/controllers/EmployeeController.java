package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EmployeeDTO;

import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.services.impl.EmployeeServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {
    private final EmployeeServiceImpl employeeServiceImpl;

    public EmployeeController(EmployeeServiceImpl employeeServiceImpl) {
        this.employeeServiceImpl = employeeServiceImpl;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public PageResponse<EmployeeDTO> getAllEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @Email String email,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) @PositiveOrZero(message = "Minimum salary must be 0 or greater") Double salaryMin,
            @RequestParam(required = false) @Positive(message = "Maximum salary must be greater than 0") Double salaryMax,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        if (salaryMin != null && salaryMax != null && salaryMin > salaryMax) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return employeeServiceImpl.getAllEmployees(name, username, email, department, position, role, salaryMin, salaryMax, PageRequest.of(page, size, sort));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(employeeServiceImpl.getById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody EmployeeDTO employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeServiceImpl.create(employeeDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @Valid @RequestBody EmployeeDTO employeeDto
    ) {
        return ResponseEntity.ok(employeeServiceImpl.update(id, employeeDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        employeeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDTO> patch(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody EmployeeDTO employeeDto
    ) {
        return ResponseEntity.ok(employeeServiceImpl.patch(id, employeeDto));
        }
}
