package com.vtit.intern.services;

import com.vtit.intern.dtos.dashboard.EvaluatedEmployeesResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface DashboardService {
    ResponseEntity<ResponseDTO<EvaluatedEmployeesResponseDTO>> getEvaluatedEmployees(Long evaluationCycleId);
}
