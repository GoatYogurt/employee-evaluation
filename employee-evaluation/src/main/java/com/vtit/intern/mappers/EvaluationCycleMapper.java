package com.vtit.intern.mappers;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.models.Employee;
import com.vtit.intern.models.EvaluationCycle;

import java.util.stream.Collectors;

public class EvaluationCycleMapper {
    public static EvaluationCycleDTO toDTO(EvaluationCycle evaluationCycle) {
        EvaluationCycleDTO evaluationCycleDTO = new EvaluationCycleDTO();
        evaluationCycleDTO.setId(evaluationCycle.getId());
        evaluationCycleDTO.setName(evaluationCycle.getName());
        evaluationCycleDTO.setDescription(evaluationCycle.getDescription());
        evaluationCycleDTO.setStartDate(evaluationCycle.getStartDate());
        evaluationCycleDTO.setEndDate(evaluationCycle.getEndDate());
        evaluationCycleDTO.setStatus(evaluationCycle.getStatus());

        evaluationCycleDTO.setEmployees(evaluationCycle.getEmployees().stream()
                .map(Employee::getId)
                .collect(Collectors.toSet()));

        evaluationCycleDTO.setManagers(evaluationCycle.getManagers().stream()
                .map(Employee::getId)
                .collect(Collectors.toSet()));

        return evaluationCycleDTO;
    }

    public static EvaluationCycle toEntity(EvaluationCycleDTO evaluationCycleDTO) {
        EvaluationCycle evaluationCycle = new EvaluationCycle();
        evaluationCycle.setId(evaluationCycleDTO.getId());
        evaluationCycle.setName(evaluationCycleDTO.getName());
        evaluationCycle.setDescription(evaluationCycleDTO.getDescription());
        evaluationCycle.setStartDate(evaluationCycleDTO.getStartDate());
        evaluationCycle.setEndDate(evaluationCycleDTO.getEndDate());
        evaluationCycle.setStatus(evaluationCycleDTO.getStatus());

        evaluationCycle.setEmployees(evaluationCycleDTO.getEmployees().stream()
                .map((id) -> {
                    Employee employee = new Employee();
                    employee.setId(id);
                    return employee;
                })
                .collect(Collectors.toSet()));

        evaluationCycle.setManagers(evaluationCycleDTO.getManagers().stream()
                .map((id) -> {
                    Employee employee = new Employee();
                    employee.setId(id);
                    return employee;
                })
                .collect(Collectors.toSet()));

        return evaluationCycle;
    }
}
