package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EvaluationScoreRequestDTO;
import com.vtit.intern.dtos.requests.MultipleEvaluationScoreRequestDTO;
import com.vtit.intern.dtos.responses.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface EvaluationScoreService {
    ResponseEntity<ResponseDTO<PageResponse<EvaluationScoreResponseDTO>>> getAll(
            Long criterionId, Long evaluationId,
            Double minScore, Double maxScore, Pageable pageable);
    ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> getById(Long id);
    ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> create(EvaluationScoreRequestDTO dto);
    ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> update(Long id, EvaluationScoreRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> delete(Long id);
    ResponseEntity<ResponseDTO<EvaluationResponseDTO>> createMultiple(MultipleEvaluationScoreRequestDTO dto);
}
