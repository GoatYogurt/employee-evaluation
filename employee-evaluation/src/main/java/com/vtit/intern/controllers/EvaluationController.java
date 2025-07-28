package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.services.EvaluationService;
import com.vtit.intern.services.impl.EvaluationServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    private final EvaluationServiceImpl evaluationServiceImpl;

    public EvaluationController(EvaluationServiceImpl evaluationServiceImpl) {
        this.evaluationServiceImpl = evaluationServiceImpl;
    }

    @PostMapping
    public ResponseEntity<EvaluationDTO> evaluate(@RequestBody EvaluationDTO evaluationDTO) {
        return ResponseEntity.status(201).body(evaluationServiceImpl.evaluate(evaluationDTO));
    }

    @GetMapping
    public PageResponse<EvaluationDTO> getEvaluations(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long criterionId,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return evaluationServiceImpl.getEvaluations(employeeId, criterionId, minScore, maxScore, comment, startDate, endDate, PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()));
    }

    @PutMapping("/{evaluationId}")
    public ResponseEntity<EvaluationDTO> updateEvaluation(@PathVariable Long evaluationId, @RequestBody EvaluationDTO evaluationDTO) {
        return ResponseEntity.ok(evaluationServiceImpl.update(evaluationId, evaluationDTO));
    }

    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long evaluationId) {
        evaluationServiceImpl.delete(evaluationId);
        return ResponseEntity.noContent().build();
    }
}
