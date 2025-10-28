package com.vtit.intern.controllers;

import com.vtit.intern.dtos.dashboard.EvaluatedEmployeesResponseDTO;
import com.vtit.intern.dtos.dashboard.ScoreDistributionResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.services.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
public class DashboardController {
    private DashboardService dashboardService;

    @GetMapping("/progress/{evaluationCycleId}")
    public ResponseEntity<ResponseDTO<EvaluatedEmployeesResponseDTO>> getCycleProgress(@PathVariable Long evaluationCycleId) {
        return dashboardService.getCycleProgress(evaluationCycleId);
    }

    @GetMapping("/distribution/{evaluationCycleId}")
    public ResponseEntity<ResponseDTO<List<ScoreDistributionResponseDTO>>> getPerformanceDistribution(@PathVariable Long evaluationCycleId) {
        return dashboardService.getScoreDistribution(evaluationCycleId);
    }

//    @GetMapping("/top")
//    public List<EmployeePerformanceDTO> getTopEmployees(@RequestParam int limit) {
//
//    }

//    @GetMapping("/bottom")
//    public List<EmployeePerformanceDTO> getBottomEmployees(@RequestParam int limit) {
//
//    }

//    @GetMapping("/average-scores")
//    public List<AverageScoreDTO> getAverageScoresOverCycles() {
//
//    }
}
