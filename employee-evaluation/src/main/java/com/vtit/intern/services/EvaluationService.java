package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EvaluationRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.searches.EvaluationSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

public interface EvaluationService {
    EvaluationResponseDTO evaluate(EvaluationRequestDTO dto);
    PageResponse<EvaluationResponseDTO> getEvaluations(EvaluationSearchDTO dto, Pageable pageable, Authentication auth);
    EvaluationResponseDTO update(Long evaluationId, EvaluationRequestDTO dto);
    void delete(Long evaluationId);
    EvaluationResponseDTO moveEvaluationToCycle(Long evaluationId, Long newCycleId);
    EvaluationResponseDTO patch(Long evaluationId, EvaluationRequestDTO dto);
}
