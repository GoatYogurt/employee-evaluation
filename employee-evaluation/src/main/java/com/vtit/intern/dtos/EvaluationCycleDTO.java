package com.vtit.intern.dtos;

import com.vtit.intern.models.Employee;
import com.vtit.intern.models.EvaluationCycleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCycleDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private EvaluationCycleStatus status;

    private Set<EvaluationDTO> evaluations;
    private Set<Long> employees;
    private Set<Long> managers;
}
