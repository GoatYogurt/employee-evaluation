package com.vtit.intern.services;

import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.dtos.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EvaluationService {
    EvaluationDTO evaluate(EvaluationDTO evaluationDTO);
    PageResponse<EvaluationDTO> getEvaluationsByEmployeeId(Long employeeId, Pageable pageable);
    EvaluationDTO update(Long evaluationId, EvaluationDTO evaluationDTO);
    void delete(Long evaluationId);
}
