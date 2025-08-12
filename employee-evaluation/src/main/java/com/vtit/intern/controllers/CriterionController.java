package com.vtit.intern.controllers;

import com.vtit.intern.dtos.searches.CriterionSearchDTO;
import com.vtit.intern.dtos.requests.CriterionRequestDTO;
import com.vtit.intern.dtos.responses.CriterionResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.services.impl.CriterionServiceImpl;
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
@RequestMapping("/api/criteria")
@Validated
public class CriterionController {
    private final CriterionServiceImpl criteriaServiceImpl;

    public CriterionController(CriterionServiceImpl criteriaService) {
        this.criteriaServiceImpl = criteriaService;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public PageResponse<CriterionResponseDTO> getAllCriteria(
            @RequestBody (required = false) CriterionSearchDTO criterionSearchDTO,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        if (criterionSearchDTO == null) {
            return criteriaServiceImpl.getAllCriteria(null, null, null, null, PageRequest.of(page, size, Sort.by(sortBy).ascending()));
        }

        if (criterionSearchDTO.getMinWeight() != null && criterionSearchDTO.getMaxWeight() != null
                && criterionSearchDTO.getMinWeight() > criterionSearchDTO.getMaxWeight()) {
            throw new IllegalArgumentException("Minimum weight cannot be greater than maximum weight");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return criteriaServiceImpl.getAllCriteria(criterionSearchDTO.getName(), criterionSearchDTO.getDescription(),
                criterionSearchDTO.getMinWeight(), criterionSearchDTO.getMaxWeight(), PageRequest.of(page, size, sort));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CriterionResponseDTO> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(criteriaServiceImpl.getById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CriterionResponseDTO> create(@Valid @RequestBody CriterionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(criteriaServiceImpl.create(dto));
    }

//    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<CriterionResponseDTO> update(
//            @PathVariable @Positive(message = "ID must be a positive number") Long id,
//            @Valid @RequestBody CriterionRequestDTO dto) {
//        return ResponseEntity.ok(criteriaServiceImpl.update(id, dto));
//    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        criteriaServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CriterionResponseDTO> patch(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @RequestBody CriterionRequestDTO dto) {
        return ResponseEntity.ok(criteriaServiceImpl.patch(id, dto));
    }
}
