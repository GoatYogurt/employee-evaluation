package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.models.Employee;
import com.vtit.intern.services.EvaluationService;
import com.vtit.intern.services.impl.EvaluationServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@Validated
public class EvaluationController {
    private final EvaluationServiceImpl evaluationServiceImpl;

    public EvaluationController(EvaluationServiceImpl evaluationServiceImpl) {
        this.evaluationServiceImpl = evaluationServiceImpl;
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<EvaluationDTO> evaluate(@Valid @RequestBody EvaluationDTO evaluationDTO) {
        return ResponseEntity.status(201).body(evaluationServiceImpl.evaluate(evaluationDTO));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public PageResponse<EvaluationDTO> getEvaluations(
            @RequestParam(required = false) @Positive(message = "Employee ID must be positive") Long employeeId,
            @RequestParam(required = false) @Positive(message = "Criterion ID must be positive") Long criterionId,
            @RequestParam(required = false) @PositiveOrZero(message = "Minimum score must be 0 or greater") Double minScore,
            @RequestParam(required = false) @PositiveOrZero(message = "Maximum score must be 0 or greater") Double maxScore,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        System.out.println("Running");
        if (minScore != null && maxScore != null && minScore > maxScore) {
            throw new IllegalArgumentException("Minimum score cannot be greater than maximum score");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authenticated user is an employee
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"))) {
            System.out.println("Authenticated as employee: " + auth.getName());
            return evaluationServiceImpl.getEvaluations(
                    ((Employee) auth.getPrincipal()).getId(), criterionId, minScore, maxScore, comment, startDate, endDate,
                    PageRequest.of(page, size,
                            sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
            );
        }

        return evaluationServiceImpl.getEvaluations(
                employeeId, criterionId, minScore, maxScore, comment, startDate, endDate,
                PageRequest.of(page, size,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PutMapping("/{evaluationId}")
    public ResponseEntity<EvaluationDTO> updateEvaluation(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId,
            @Valid @RequestBody EvaluationDTO evaluationDTO
    ) {
        return ResponseEntity.ok(evaluationServiceImpl.update(evaluationId, evaluationDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId
    ) {
        evaluationServiceImpl.delete(evaluationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{evaluationId}/move")
    public ResponseEntity<EvaluationDTO> moveEvaluationToCycle(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId,
            @RequestParam @Positive(message = "New cycle ID must be positive") Long newCycleId
    ) {
        return ResponseEntity.ok(evaluationServiceImpl.moveEvaluationToCycle(evaluationId, newCycleId));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{evaluationId}")
    public ResponseEntity<EvaluationDTO> patchEvaluation(
            @PathVariable @Positive(message = "Evaluation ID must be positive") Long evaluationId,
            @Valid @RequestBody EvaluationDTO evaluationDTO
    ) {
        return ResponseEntity.ok(evaluationServiceImpl.patch(evaluationId, evaluationDTO));
    }
}
