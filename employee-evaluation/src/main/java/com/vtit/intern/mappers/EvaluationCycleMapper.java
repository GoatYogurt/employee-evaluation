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
        evaluationCycleResponseDTO.setStartDate(evaluationCycle.getStartDate());
        evaluationCycleResponseDTO.setEndDate(evaluationCycle.getEndDate());
        evaluationCycleResponseDTO.setStatus(evaluationCycle.getStatus());

        evaluationCycleResponseDTO.setEmployees(evaluationCycle.getEmployees().stream()
                .map(Employee::getId)
                .collect(Collectors.toSet()));

        evaluationCycleResponseDTO.setManagers(evaluationCycle.getManagers().stream()
                .map(Employee::getId)
                .collect(Collectors.toSet()));

        evaluationCycleResponseDTO.setEvaluations(
                evaluationCycle.getEvaluations() == null ? null :
                        evaluationCycle.getEvaluations().stream()
                                .map(e -> {
                                    EvaluationResponseDTO eDto = new EvaluationResponseDTO();
                                    eDto.setScore(e.getScore());
                                    eDto.setComment(e.getComment());
                                    eDto.setEvaluationDate(e.getEvaluationDate());
                                    eDto.setEmployeeId(e.getEmployee() != null ? e.getEmployee().getId() : null);
                                    eDto.setCriterionId(e.getCriterion() != null ? e.getCriterion().getId() : null);
                                    eDto.setEvaluationCycleId(evaluationCycle.getId());
                                    return eDto;
                                })
                                .collect(Collectors.toSet())
        );

        return evaluationCycleResponseDTO;
    }

    public static EvaluationCycle requestToEntity(EvaluationCycleRequestDTO dto) {
        EvaluationCycle evaluationCycle = new EvaluationCycle();
        evaluationCycle.setId(dto.getId());
        evaluationCycle.setName(dto.getName());
        evaluationCycle.setDescription(dto.getDescription());
        evaluationCycle.setStartDate(dto.getStartDate());
        evaluationCycle.setEndDate(dto.getEndDate());
        evaluationCycle.setStatus(dto.getStatus());

        evaluationCycle.setEmployees(dto.getEmployees().stream()
                .map((id) -> {
                    Employee employee = new Employee();
                    employee.setId(id);
                    return employee;
                })
                .collect(Collectors.toSet()));

        evaluationCycle.setManagers(dto.getManagers().stream()
                .map((id) -> {
                    Employee employee = new Employee();
                    employee.setId(id);
                    return employee;
                })
                .collect(Collectors.toSet()));

        return evaluationCycle;
    }
}
