package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.EmployeeRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.searches.EmployeeSearchDTO;
import com.vtit.intern.services.EmployeeService;
import com.vtit.intern.services.impl.EmployeeServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public PageResponse<EmployeeResponseDTO> getAllEmployees(
            @RequestBody(required = false) EmployeeSearchDTO dto,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        if (dto == null) {
            return employeeService.getAllEmployees(null, null, null, null, null, null, null, null,
                    PageRequest.of(page, size, Sort.by(sortBy).ascending()));
        }

        if (dto.getSalaryMin() != null && dto.getSalaryMax() != null && dto.getSalaryMin() > dto.getSalaryMax()) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return employeeService.getAllEmployees(dto.getName(), dto.getUsername(), dto.getEmail(), dto.getDepartment(),
                dto.getPosition(), dto.getRole(), dto.getSalaryMin(), dto.getSalaryMax(), PageRequest.of(page, size, sort));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(dto));
    }

//    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<EmployeeResponseDTO> update(
//            @PathVariable @Positive(message = "ID must be a positive number") Long id,
//            @Valid @RequestBody EmployeeRequestDTO dto
//    ) {
//        return ResponseEntity.ok(employeeService.update(id, dto));
//    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> patch(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody EmployeeRequestDTO dto
    ) {
        return ResponseEntity.ok(employeeService.patch(id, dto));
        }
}
