package com.vtit.intern.controllers;

import com.vtit.intern.dtos.CriterionDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.models.Criterion;
import com.vtit.intern.services.impl.CriterionServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/criteria")
@Validated
public class CriterionController {
    private final CriterionServiceImpl criteriaServiceImpl;

    public CriterionController(CriterionServiceImpl criteriaService ) {
        this.criteriaServiceImpl = criteriaService;
    }

    @GetMapping
    public PageResponse<CriterionDTO> getAllCriteria(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @PositiveOrZero(message = "Minimum weight must be 0 or greater") Double minWeight,
            @RequestParam(required = false) @PositiveOrZero(message = "Maximum weight must be 0 or greater") Double maxWeight,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        if (minWeight != null && maxWeight != null && minWeight > maxWeight) {
            throw new IllegalArgumentException("Minimum weight cannot be greater than maximum weight");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return criteriaServiceImpl.getAllCriteria(name, description, minWeight, maxWeight, PageRequest.of(page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionDTO> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(criteriaServiceImpl.getById(id));
    }

    @PostMapping
    public ResponseEntity<CriterionDTO> create(@Valid @RequestBody CriterionDTO criterionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(criteriaServiceImpl.create(criterionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionDTO> update(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @Valid @RequestBody CriterionDTO criterionDto) {
        return ResponseEntity.ok(criteriaServiceImpl.update(id, criterionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        criteriaServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
