package com.vtit.intern.services;

import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.dtos.PageResponse;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface EvaluationService {
    EvaluationDTO evaluate(EvaluationDTO evaluationDTO);
    PageResponse<EvaluationDTO> getEvaluations(Long employeeId, Long criterionId, Double minScore, Double maxScore, String comment, LocalDate startDate, LocalDate endDate, Pageable pageable);
    EvaluationDTO update(Long evaluationId, EvaluationDTO evaluationDTO);
    void delete(Long evaluationId);
}
