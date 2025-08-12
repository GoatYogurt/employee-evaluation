package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EvaluationRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface EvaluationService {
    EvaluationResponseDTO evaluate(EvaluationRequestDTO dto);
    PageResponse<EvaluationResponseDTO> getEvaluations(Long employeeId, Long criterionId, Double minScore, Double maxScore, String comment, LocalDate startDate, LocalDate endDate, Pageable pageable);
    EvaluationResponseDTO update(Long evaluationId, EvaluationRequestDTO dto);
    void delete(Long evaluationId);
    EvaluationResponseDTO moveEvaluationToCycle(Long evaluationId, Long newCycleId);
    EvaluationResponseDTO patch(Long evaluationId, EvaluationRequestDTO dto);
}
