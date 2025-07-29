package com.vtit.intern.services;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.models.EvaluationCycleStatus;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface EvaluationCycleService {
    EvaluationCycleDTO create(EvaluationCycleDTO evaluationCycleDTO);
    EvaluationCycleDTO get(Long id);
    EvaluationCycleDTO update(Long id, EvaluationCycleDTO evaluationCycleDTO);
    void delete(Long id);
    PageResponse<EvaluationCycleDTO> getAllEvaluationCycles(String name, String description, EvaluationCycleStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
    PageResponse<EvaluationCycleDTO> getActiveEvaluationCycles(Pageable pageable);
    EvaluationCycleDTO patch(Long id, EvaluationCycleDTO evaluationCycleDTO);
}
