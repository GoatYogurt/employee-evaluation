package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.models.EvaluationCycleStatus;
import com.vtit.intern.services.impl.EvaluationCycleServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evaluation-cycles")
@Validated
public class EvaluationCycleController {
    private final EvaluationCycleServiceImpl evaluationCycleServiceImpl;

    public EvaluationCycleController(EvaluationCycleServiceImpl evaluationCycleServiceImpl) {
        this.evaluationCycleServiceImpl = evaluationCycleServiceImpl;
    }


    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public PageResponse<EvaluationCycleDTO> getAllEvaluationCycles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) EvaluationCycleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return evaluationCycleServiceImpl.getAllEvaluationCycles(
                name, description, status, startDate, endDate,
                PageRequest.of(page, size,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCycleDTO> getEvaluationCycleById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(evaluationCycleServiceImpl.get(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE, ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/active")
    public PageResponse<EvaluationCycleDTO> getActiveEvaluationCycles(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        return evaluationCycleServiceImpl.getActiveEvaluationCycles(
                PageRequest.of(page, size,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<EvaluationCycleDTO> createEvaluationCycle(@Valid @RequestBody EvaluationCycleDTO evaluationCycleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluationCycleServiceImpl.create(evaluationCycleDTO));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationCycleDTO> updateEvaluationCycle(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody EvaluationCycleDTO evaluationCycleDTO
    ) {
        return ResponseEntity.ok(evaluationCycleServiceImpl.update(id, evaluationCycleDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteEvaluationCycle(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        evaluationCycleServiceImpl.delete(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EvaluationCycleDTO> patchEvaluationCycle(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody EvaluationCycleDTO evaluationCycleDTO
    ) {
        return ResponseEntity.ok(evaluationCycleServiceImpl.patch(id, evaluationCycleDTO));
    }
}
