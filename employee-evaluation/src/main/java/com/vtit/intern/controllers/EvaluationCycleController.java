package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.models.EvaluationCycleStatus;
import com.vtit.intern.services.impl.EvaluationCycleServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evaluation-cycles")
public class EvaluationCycleController {
    private final EvaluationCycleServiceImpl evaluationCycleServiceImpl;

    public EvaluationCycleController(EvaluationCycleServiceImpl evaluationCycleServiceImpl) {
        this.evaluationCycleServiceImpl = evaluationCycleServiceImpl;
    }

    @GetMapping
    public PageResponse<EvaluationCycleDTO> getAllEvaluationCycles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) EvaluationCycleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return evaluationCycleServiceImpl.getAllEvaluationCycles(name, description, status, startDate, endDate, PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCycleDTO> getEvaluationCycleById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationCycleServiceImpl.get(id));
    }

    @GetMapping("/active")
    public PageResponse<EvaluationCycleDTO> getActiveEvaluationCycles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return evaluationCycleServiceImpl.getActiveEvaluationCycles(PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()));
    }

    @PostMapping
    public ResponseEntity<EvaluationCycleDTO> createEvaluationCycle(@RequestBody EvaluationCycleDTO evaluationCycleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluationCycleServiceImpl.create(evaluationCycleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationCycleDTO> updateEvaluationCycle(@PathVariable Long id, @RequestBody EvaluationCycleDTO evaluationCycleDTO) {
        return ResponseEntity.ok(evaluationCycleServiceImpl.update(id, evaluationCycleDTO));
    }

    @DeleteMapping("/{id}")
    public void deleteEvaluationCycle(@PathVariable Long id) {
        evaluationCycleServiceImpl.delete(id);
    }
}
