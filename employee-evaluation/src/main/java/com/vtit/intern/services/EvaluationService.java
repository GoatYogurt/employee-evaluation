package com.vtit.intern.services;

import com.vtit.intern.dtos.EvaluationDTO;

import java.util.List;

public interface EvaluationService {
    EvaluationDTO evaluate(EvaluationDTO evaluationDTO);
    List<EvaluationDTO> getEvaluationsByEmployeeId(Long employeeId);
    EvaluationDTO update(Long evaluationId, EvaluationDTO evaluationDTO);
    void delete(Long evaluationId);
}
