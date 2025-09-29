package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.EvaluationRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.searches.EvaluationSearchDTO;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.services.EvaluationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
@Validated
public class EvaluationController {
    private final EvaluationService evaluationService;
    private final EmployeeRepository employeeRepository;

    public EvaluationController(EvaluationService evaluationService, EmployeeRepository employeeRepository) {
        this.evaluationService = evaluationService;
        this.employeeRepository = employeeRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<EvaluationResponseDTO> evaluate(@Valid @RequestBody EvaluationRequestDTO dto) {
        return ResponseEntity.status(201).body(evaluationService.evaluate(dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public PageResponse<EvaluationResponseDTO> getEvaluations(
            @RequestBody(required = false) EvaluationSearchDTO dto,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        return evaluationService.getEvaluations(dto,
                PageRequest.of(page, size, sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()),
                SecurityContextHolder.getContext().getAuthentication());
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PutMapping("/{evaluationId}")
    public ResponseEntity<EvaluationResponseDTO> updateEvaluation(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId,
            @Valid @RequestBody EvaluationRequestDTO dto
    ) {
        return ResponseEntity.ok(evaluationService.update(evaluationId, dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId
    ) {
        evaluationService.delete(evaluationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{evaluationId}/move")
    public ResponseEntity<EvaluationResponseDTO> moveEvaluationToCycle(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId,
            @RequestParam @Positive(message = "New cycle ID must be positive") Long newCycleId
    ) {
        return ResponseEntity.ok(evaluationService.moveEvaluationToCycle(evaluationId, newCycleId));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{evaluationId}")
    public ResponseEntity<EvaluationResponseDTO> patchEvaluation(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId,
            @RequestBody EvaluationRequestDTO dto
    ) {
        return ResponseEntity.ok(evaluationService.patch(evaluationId, dto));
    }
}
