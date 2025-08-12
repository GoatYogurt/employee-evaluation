package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.EvaluationCycleRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationCycleResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.services.impl.EvaluationCycleServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public PageResponse<EvaluationCycleResponseDTO> getAllEvaluationCycles(
            @RequestBody(required = false) EvaluationCycleRequestDTO dto,
            @RequestParam(defaultValue = "0")@Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        if (dto == null) {
            return evaluationCycleServiceImpl.getAllEvaluationCycles(
                    null, null, null, null, null,
                    PageRequest.of(page, size, sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
            );
        }

        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return evaluationCycleServiceImpl.getAllEvaluationCycles(
                dto.getName(), dto.getDescription(), dto.getStatus(), dto.getStartDate(), dto.getEndDate(),
                PageRequest.of(page, size,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCycleResponseDTO> getEvaluationCycleById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(evaluationCycleServiceImpl.get(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/active")
    public PageResponse<EvaluationCycleResponseDTO> getActiveEvaluationCycles(
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
    public ResponseEntity<EvaluationCycleResponseDTO> createEvaluationCycle(@Valid @RequestBody EvaluationCycleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluationCycleServiceImpl.create(dto));
    }

//    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<EvaluationCycleResponseDTO> updateEvaluationCycle(
//            @PathVariable @Positive(message = "ID must be a positive number") Long id,
//            @RequestBody @Valid EvaluationCycleRequestDTO dto
//    ) {
//        return ResponseEntity.ok(evaluationCycleServiceImpl.update(id, dto));
//    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteEvaluationCycle(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        evaluationCycleServiceImpl.delete(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EvaluationCycleResponseDTO> patchEvaluationCycle(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody EvaluationCycleRequestDTO dto
    ) {
        return ResponseEntity.ok(evaluationCycleServiceImpl.patch(id, dto));
    }
}
