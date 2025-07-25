package com.vtit.intern.services;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import java.util.List;

public interface EvaluationCycleService {
    EvaluationCycleDTO create(EvaluationCycleDTO evaluationCycleDTO);
    EvaluationCycleDTO get(Long id);
    EvaluationCycleDTO update(Long id, EvaluationCycleDTO evaluationCycleDTO);
    void delete(Long id);
    List<EvaluationCycleDTO> getAllEvaluationCycles();
    List<EvaluationCycleDTO> getActiveEvaluationCycles();
}
