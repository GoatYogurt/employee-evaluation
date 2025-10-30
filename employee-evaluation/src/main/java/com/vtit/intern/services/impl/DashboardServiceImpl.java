package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.dashboard.AverageScoreResponseDTO;
import com.vtit.intern.dtos.dashboard.EmployeePerformanceResponseDTO;
import com.vtit.intern.dtos.dashboard.ScoreDistributionResponseDTO;
import com.vtit.intern.models.Evaluation;
import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.repositories.EvaluationCycleRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.services.DashboardService;
import com.vtit.intern.utils.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.vtit.intern.dtos.dashboard.EvaluatedEmployeesResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private EvaluationCycleRepository evaluationCycleRepository;
    private EvaluationRepository evaluationRepository;

    private static final List<String> RANGES = List.of("0–1.99", "2–3.99", "4–5");

    @Override
    public ResponseEntity<ResponseDTO<EvaluatedEmployeesResponseDTO>> getCycleProgress(Long evaluationCycleId) {
        Optional<EvaluationCycle> optionalEvaluationCycle = evaluationCycleRepository.findById(evaluationCycleId);
        if (optionalEvaluationCycle.isEmpty()) {
            return ResponseUtil.notFound("Evaluation cycle with id " + evaluationCycleId + " not found");
        }

        EvaluationCycle evaluationCycle = optionalEvaluationCycle.get();
        Object result = evaluationRepository.getEvaluationProgressRaw(evaluationCycleId);
        Object[] row = (Object[]) result;
        int totalAssigned = ((Number) row[0]).intValue();
        int totalEvaluated = ((Number) row[1]).intValue();

        EvaluatedEmployeesResponseDTO responseDTO = new EvaluatedEmployeesResponseDTO();
        responseDTO.setCycleName(evaluationCycle.getName());
        responseDTO.setTotalCount(totalAssigned);
        responseDTO.setEvaluatedCount(totalEvaluated);
        responseDTO.setNotEvaluatedCount(totalAssigned - totalEvaluated);

        return ResponseUtil.success(responseDTO);
    }

    public ResponseEntity<ResponseDTO<List<ScoreDistributionResponseDTO>>> getScoreDistribution(Long evaluationCycleId) {
        List<Object[]> raw = evaluationRepository.getScoreDistributionRaw(evaluationCycleId);

        Map<String, Long> map = raw.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));

        return ResponseUtil.success(RANGES.stream()
                .map(range -> new ScoreDistributionResponseDTO(range, map.getOrDefault(range, 0L)))
                .toList());
    }

    public ResponseEntity<ResponseDTO<List<EmployeePerformanceResponseDTO>>> getTopEmployees(Long evaluationCycleId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Evaluation> topEvaluations = evaluationRepository.findTopEvaluations(evaluationCycleId, pageable);

        return ResponseUtil.success(topEvaluations.stream().map(
                eval -> new EmployeePerformanceResponseDTO(
                        eval.getEmployee().getId(),
                        eval.getEmployee().getFullName(),
                        eval.getTotalScore()
                )
        ).toList());
    }

    @Override
    public ResponseEntity<ResponseDTO<List<EmployeePerformanceResponseDTO>>> getBottomEmployees(Long evaluationCycleId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Evaluation> bottomEvaluations = evaluationRepository.findBottomEvaluations(evaluationCycleId, pageable);

        return ResponseUtil.success(bottomEvaluations.stream().map(
                eval -> new EmployeePerformanceResponseDTO(
                        eval.getEmployee().getId(),
                        eval.getEmployee().getFullName(),
                        eval.getTotalScore()
                )
        ).toList());
    }

    @Override
    public ResponseEntity<ResponseDTO<List<AverageScoreResponseDTO>>> getAverageScoresOverCycles() {
        List<Object[]> raw = evaluationRepository.getAverageScoresOverCyclesRaw();

        List<AverageScoreResponseDTO> responseDTOs = raw.stream()
                .map(r -> new AverageScoreResponseDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).doubleValue()
                ))
                .sorted(Comparator.comparing(AverageScoreResponseDTO::getEvaluationCycleId))
                .toList();


        return ResponseUtil.success(responseDTOs);
    }
}
