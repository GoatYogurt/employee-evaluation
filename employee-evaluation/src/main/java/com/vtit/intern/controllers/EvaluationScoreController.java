package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.EvaluationScoreRequestDTO;
import com.vtit.intern.dtos.responses.*;
import com.vtit.intern.services.EvaluationScoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation-scores")
public class EvaluationScoreController {

    private final EvaluationScoreService evaluationScoreService;

    @Autowired
    public EvaluationScoreController(EvaluationScoreService evaluationScoreService) {
        this.evaluationScoreService = evaluationScoreService;
    }

    // Lấy danh sách có filter + phân trang
    @GetMapping
    public ResponseEntity<ResponseDTO<PageResponse<EvaluationScoreResponseDTO>>> getAll(
            @RequestParam(required = false) Long criterionId,
            @RequestParam(required = false) Long evaluationId,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            Pageable pageable
    ) {
        return evaluationScoreService.getAll(criterionId, evaluationId, minScore, maxScore, pageable);
    }

    // Lấy chi tiết theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return evaluationScoreService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> create(
            @Valid @RequestBody EvaluationScoreRequestDTO dto
    ) {
        return evaluationScoreService.create(dto);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationScoreRequestDTO dto
    ) {
        return evaluationScoreService.update(id, dto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        evaluationScoreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
