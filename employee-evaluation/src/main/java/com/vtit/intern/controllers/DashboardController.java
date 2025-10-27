package com.vtit.intern.controllers;

import com.vtit.intern.dtos.dashboard.EvaluatedEmployeesResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.services.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
public class DashboardController {
    private DashboardService dashboardService;

    @GetMapping("/evaluated-employees/{evaluationCycleId}")
    public ResponseEntity<ResponseDTO<EvaluatedEmployeesResponseDTO>> getEvaluatedEmployees(@PathVariable Long evaluationCycleId) {
        return dashboardService.getEvaluatedEmployees(evaluationCycleId);
    }
}
