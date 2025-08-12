package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EvaluationCycleRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationCycleResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.models.EvaluationCycleStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface EvaluationCycleService {
    EvaluationCycleResponseDTO create(EvaluationCycleRequestDTO evaluationCycleRequestDTO);
    EvaluationCycleResponseDTO get(Long id);
//    EvaluationCycleResponseDTO update(Long id, EvaluationCycleRequestDTO dto);
    void delete(Long id);
    PageResponse<EvaluationCycleResponseDTO> getAllEvaluationCycles(String name, String description, EvaluationCycleStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
    PageResponse<EvaluationCycleResponseDTO> getActiveEvaluationCycles(Pageable pageable);
    EvaluationCycleResponseDTO patch(Long id, EvaluationCycleRequestDTO evaluationCycleRequestDTO);
}
