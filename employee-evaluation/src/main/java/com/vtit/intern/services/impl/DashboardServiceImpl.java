package com.vtit.intern.services.impl;

import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.repositories.EvaluationCycleRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.services.DashboardService;
import com.vtit.intern.utils.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.vtit.intern.dtos.dashboard.EvaluatedEmployeesResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private EvaluationCycleRepository evaluationCycleRepository;
    private EvaluationRepository evaluationRepository;

    @Override
    public ResponseEntity<ResponseDTO<EvaluatedEmployeesResponseDTO>> getEvaluatedEmployees(Long evaluationCycleId) {
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
}
