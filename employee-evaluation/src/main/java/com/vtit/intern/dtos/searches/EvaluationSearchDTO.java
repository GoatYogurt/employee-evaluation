package com.vtit.intern.dtos.searches;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationSearchDTO {
    private Long employeeId;
    private Long evaluationCycleId;
    private Long projectId;
    private Double minScore;
    private Double maxScore;
    private String comment;
    private LocalDate startDate;
    private LocalDate endDate;
}
