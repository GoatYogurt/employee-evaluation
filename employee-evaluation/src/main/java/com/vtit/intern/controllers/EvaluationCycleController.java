package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.EvaluationCycleRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationCycleResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.services.EvaluationCycleService;
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
    private final EvaluationCycleService evaluationCycleService;

    public EvaluationCycleController(EvaluationCycleService evaluationCycleService) {
        this.evaluationCycleService = evaluationCycleService;
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
            return evaluationCycleService.getAllEvaluationCycles(
                    null, null, null, null, null,
                    PageRequest.of(page, size, sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
            );
        }

        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return evaluationCycleService.getAllEvaluationCycles(
                dto.getName(), dto.getDescription(), dto.getStatus(), dto.getStartDate(), dto.getEndDate(),
                PageRequest.of(page, size,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCycleResponseDTO> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(evaluationCycleService.get(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/active")
    public PageResponse<EvaluationCycleResponseDTO> getActive(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        return evaluationCycleService.getActiveEvaluationCycles(
                PageRequest.of(page, size,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<EvaluationCycleResponseDTO> create(@Valid @RequestBody EvaluationCycleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluationCycleService.create(dto));
    }

//    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<EvaluationCycleResponseDTO> updateEvaluationCycle(
//            @PathVariable @Positive(message = "ID must be a positive number") Long id,
//            @RequestBody @Valid EvaluationCycleRequestDTO dto
//    ) {
//        return ResponseEntity.ok(evaluationCycleService.update(id, dto));
//    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id) {
        evaluationCycleService.delete(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EvaluationCycleResponseDTO> patch(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody EvaluationCycleRequestDTO dto
    ) {
        return ResponseEntity.ok(evaluationCycleService.patch(id, dto));
    }
}
