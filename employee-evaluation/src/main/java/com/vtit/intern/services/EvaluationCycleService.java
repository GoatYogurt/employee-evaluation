package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EvaluationCycleRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationCycleResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.enums.EvaluationCycleStatus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface EvaluationCycleService {
    ResponseEntity<ResponseDTO<EvaluationCycleResponseDTO>> create(EvaluationCycleRequestDTO evaluationCycleRequestDTO);
    ResponseEntity<ResponseDTO<EvaluationCycleResponseDTO>> get(Long id);
//    EvaluationCycleResponseDTO update(Long id, EvaluationCycleRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> delete(Long id);
    ResponseEntity<ResponseDTO<PageResponse<EvaluationCycleResponseDTO>>> getAllEvaluationCycles(String name, String description, EvaluationCycleStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
    ResponseEntity<ResponseDTO<PageResponse<EvaluationCycleResponseDTO>>> getActiveEvaluationCycles(Pageable pageable);
    ResponseEntity<ResponseDTO<PageResponse<ProjectResponseDTO>>> getProjectsByEvaluationCycleId(Long evaluationCycleId, Pageable pageable);
    ResponseEntity<ResponseDTO<EvaluationCycleResponseDTO>> patch(Long id, EvaluationCycleRequestDTO evaluationCycleRequestDTO);
    ResponseEntity<InputStreamResource> exportEvaluationCycleReport(Long evaluationCycleId);
}
