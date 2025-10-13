package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.ProjectRequestDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.ProjectSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


public interface ProjectService {
    ResponseEntity<ResponseDTO<ProjectResponseDTO>> create(ProjectRequestDTO dto);
    ResponseEntity<ResponseDTO<ProjectResponseDTO>> getById(Long id);
    ResponseEntity<ResponseDTO<PageResponse<ProjectResponseDTO>>> getAll(ProjectSearchDTO searchDTO, Pageable pageable);
    ResponseEntity<ResponseDTO<ProjectResponseDTO>> patch(Long id, ProjectRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> delete(Long id);
    ResponseEntity<ResponseDTO<Void>> addProjectToEvaluationCycle(Long projectId, Long evaluationCycleId);
    ResponseEntity<ResponseDTO<Void>> removeProjectFromEvaluationCycle(Long projectId, Long evaluationCycleId);
    ResponseEntity<ResponseDTO<Void>> addEmployeeToProject(Long projectId, Long employeeId);
    ResponseEntity<ResponseDTO<Void>> removeEmployeeFromProject(Long projectId, Long employeeId);
}