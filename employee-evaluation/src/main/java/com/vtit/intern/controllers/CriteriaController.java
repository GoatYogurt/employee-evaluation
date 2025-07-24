package com.vtit.intern.controllers;

import com.vtit.intern.dtos.CriteriaDTO;
import com.vtit.intern.models.Criteria;
import com.vtit.intern.services.impl.CriteriaServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/criteria")
public class CriteriaController {
    private final CriteriaServiceImpl criteriaServiceImpl;
    private final ModelMapper modelMapper;

    public CriteriaController(CriteriaServiceImpl criteriaService, ModelMapper modelMapper) {
        this.criteriaServiceImpl = criteriaService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<CriteriaDTO> getAllCriteria() {
        return criteriaServiceImpl.getAllCriteria().stream()
                .map(criteria -> modelMapper.map(criteria, CriteriaDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriteriaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(modelMapper.map(criteriaServiceImpl.getById(id), CriteriaDTO.class));
    }

    @PostMapping
    public ResponseEntity<CriteriaDTO> create(@RequestBody CriteriaDTO criteriaDto) {
        Criteria criteriaToCreate = modelMapper.map(criteriaDto, Criteria.class);
        Criteria createdCriteria = criteriaServiceImpl.create(criteriaToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdCriteria, CriteriaDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriteriaDTO> update(@PathVariable Long id, @RequestBody CriteriaDTO criteriaDto) {
        Criteria updatedCriteria = modelMapper.map(criteriaDto, Criteria.class);
        Criteria savedCriteria = criteriaServiceImpl.update(id, updatedCriteria);
        return ResponseEntity.ok(modelMapper.map(savedCriteria, CriteriaDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        criteriaServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
