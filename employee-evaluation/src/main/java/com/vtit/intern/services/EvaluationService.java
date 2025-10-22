package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EvaluationRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.EvaluationSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

public interface EvaluationService {
    ResponseEntity<ResponseDTO<EvaluationResponseDTO>> create(EvaluationRequestDTO dto);
    ResponseEntity<ResponseDTO<PageResponse<EvaluationResponseDTO>>> getEvaluations(EvaluationSearchDTO dto, Pageable pageable, Authentication auth);
    ResponseEntity<ResponseDTO<EvaluationResponseDTO>> getById(Long evaluationId);
    ResponseEntity<ResponseDTO<EvaluationResponseDTO>> update(Long evaluationId, EvaluationRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> delete(Long evaluationId);
    ResponseEntity<ResponseDTO<EvaluationResponseDTO>> moveEvaluationToCycle(Long evaluationId, Long newCycleId);
    ResponseEntity<ResponseDTO<EvaluationResponseDTO>> patch(Long evaluationId, EvaluationRequestDTO dto);
}
