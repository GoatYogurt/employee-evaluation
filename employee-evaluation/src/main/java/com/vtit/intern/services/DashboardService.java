package com.vtit.intern.services;

import com.vtit.intern.dtos.dashboard.EmployeePerformanceResponseDTO;
import com.vtit.intern.dtos.dashboard.EvaluatedEmployeesResponseDTO;
import com.vtit.intern.dtos.dashboard.ScoreDistributionResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DashboardService {
    ResponseEntity<ResponseDTO<EvaluatedEmployeesResponseDTO>> getCycleProgress(Long evaluationCycleId);
    ResponseEntity<ResponseDTO<List<ScoreDistributionResponseDTO>>> getScoreDistribution(Long evaluationCycleId);
    ResponseEntity<ResponseDTO<List<EmployeePerformanceResponseDTO>>> getTopEmployees(Long evaluationCycleId, int limit);
}
