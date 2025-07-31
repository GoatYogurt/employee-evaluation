package com.vtit.intern.services;

import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.responses.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface EvaluationService {
    EvaluationDTO evaluate(EvaluationDTO evaluationDTO);
    PageResponse<EvaluationDTO> getEvaluations(Long employeeId, Long criterionId, Double minScore, Double maxScore, String comment, LocalDate startDate, LocalDate endDate, Pageable pageable);
    EvaluationDTO update(Long evaluationId, EvaluationDTO evaluationDTO);
    void delete(Long evaluationId);
    EvaluationDTO moveEvaluationToCycle(Long evaluationId, Long newCycleId);
    EvaluationDTO patch(Long evaluationId, EvaluationDTO evaluationDTO);
}
