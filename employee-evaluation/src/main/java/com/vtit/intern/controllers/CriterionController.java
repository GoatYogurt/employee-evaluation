package com.vtit.intern.controllers;

import com.vtit.intern.dtos.CriterionDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.models.Criterion;
import com.vtit.intern.services.impl.CriterionServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/criteria")
public class CriterionController {
    private final CriterionServiceImpl criteriaServiceImpl;

    public CriterionController(CriterionServiceImpl criteriaService ) {
        this.criteriaServiceImpl = criteriaService;
    }

    @GetMapping
    public PageResponse<CriterionDTO> getAllCriteria(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minWeight,
            @RequestParam(required = false) Double maxWeight,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return criteriaServiceImpl.getAllCriteria(name, description, minWeight, maxWeight, PageRequest.of(page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(criteriaServiceImpl.getById(id));
    }

    @PostMapping
    public ResponseEntity<CriterionDTO> create(@RequestBody CriterionDTO criterionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(criteriaServiceImpl.create(criterionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionDTO> update(@PathVariable Long id, @RequestBody CriterionDTO criterionDto) {
        return ResponseEntity.ok(criteriaServiceImpl.update(id, criterionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        criteriaServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
