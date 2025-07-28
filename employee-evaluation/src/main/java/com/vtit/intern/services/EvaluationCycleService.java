package com.vtit.intern.services;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.dtos.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EvaluationCycleService {
    EvaluationCycleDTO create(EvaluationCycleDTO evaluationCycleDTO);
    EvaluationCycleDTO get(Long id);
    EvaluationCycleDTO update(Long id, EvaluationCycleDTO evaluationCycleDTO);
    void delete(Long id);
    PageResponse<EvaluationCycleDTO> getAllEvaluationCycles(Pageable pageable);
    PageResponse<EvaluationCycleDTO> getActiveEvaluationCycles(Pageable pageable);
}
