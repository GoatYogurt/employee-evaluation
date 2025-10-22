package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.EmployeeRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.EmployeeSearchDTO;
import com.vtit.intern.services.EmployeeService;
import com.vtit.intern.services.impl.EmployeeServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseDTO<PageResponse<EmployeeResponseDTO>>> getAllEmployees(
            @RequestBody(required = false) EmployeeSearchDTO dto,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        if (dto == null) {
            return employeeService.getAllEmployees(null, PageRequest.of(page, size, Sort.by(sortBy).ascending()));
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return employeeService.getAllEmployees(dto, PageRequest.of(page, size, sort));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return employeeService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return employeeService.create(dto);
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
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable @Positive(message = "ID must be a positive number") Long id) {
        return employeeService.delete(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> patch(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody EmployeeRequestDTO dto
    ) {
        return employeeService.patch(id, dto);
    }
}
