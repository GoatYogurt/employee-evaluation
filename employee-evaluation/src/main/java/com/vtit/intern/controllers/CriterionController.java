package com.vtit.intern.controllers;

import com.vtit.intern.dtos.CriterionDTO;
import com.vtit.intern.models.Criterion;
import com.vtit.intern.services.impl.CriterionServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/criteria")
public class CriterionController {
    private final CriterionServiceImpl criteriaServiceImpl;
    private final ModelMapper modelMapper;

    public CriterionController(CriterionServiceImpl criteriaService, ModelMapper modelMapper) {
        this.criteriaServiceImpl = criteriaService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<CriterionDTO> getAllCriteria() {
        return criteriaServiceImpl.getAllCriteria().stream()
                .map(criterion -> modelMapper.map(criterion, CriterionDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(modelMapper.map(criteriaServiceImpl.getById(id), CriterionDTO.class));
    }

    @PostMapping
    public ResponseEntity<CriterionDTO> create(@RequestBody CriterionDTO criterionDto) {
        Criterion criterionToCreate = modelMapper.map(criterionDto, Criterion.class);
        Criterion createdCriterion = criteriaServiceImpl.create(criterionToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdCriterion, CriterionDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionDTO> update(@PathVariable Long id, @RequestBody CriterionDTO criterionDto) {
        Criterion updatedCriterion = modelMapper.map(criterionDto, Criterion.class);
        Criterion savedCriterion = criteriaServiceImpl.update(id, updatedCriterion);
        return ResponseEntity.ok(modelMapper.map(savedCriterion, CriterionDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        criteriaServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
