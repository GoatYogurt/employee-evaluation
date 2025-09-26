package com.vtit.intern.mappers;

import com.vtit.intern.dtos.requests.EvaluationCycleRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationCycleResponseDTO;
import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.models.Employee;
import com.vtit.intern.models.EvaluationCycle;

import java.util.stream.Collectors;

public class EvaluationCycleMapper {
    public static EvaluationCycleResponseDTO entityToResponse(EvaluationCycle evaluationCycle) {
        EvaluationCycleResponseDTO evaluationCycleResponseDTO = new EvaluationCycleResponseDTO();
        evaluationCycleResponseDTO.setName(evaluationCycle.getName());
        evaluationCycleResponseDTO.setDescription(evaluationCycle.getDescription());
        evaluationCycleResponseDTO.setStartDate(evaluationCycle.getStartDate().toLocalDate());
        evaluationCycleResponseDTO.setEndDate(evaluationCycle.getEndDate().toLocalDate());
        evaluationCycleResponseDTO.setStatus(evaluationCycle.getStatus());

        return evaluationCycleResponseDTO;
    }

    public static EvaluationCycle requestToEntity(EvaluationCycleRequestDTO dto) {
        EvaluationCycle evaluationCycle = new EvaluationCycle();
        evaluationCycle.setId(dto.getId());
        evaluationCycle.setName(dto.getName());
        evaluationCycle.setDescription(dto.getDescription());
        evaluationCycle.setStartDate(dto.getStartDate().atStartOfDay());
        evaluationCycle.setEndDate(dto.getEndDate().atStartOfDay());
        evaluationCycle.setStatus(dto.getStatus());

        return evaluationCycle;
    }
}
