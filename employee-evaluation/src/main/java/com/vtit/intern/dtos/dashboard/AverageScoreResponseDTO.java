package com.vtit.intern.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AverageScoreResponseDTO {
    private Long evaluationCycleId;
    private String evaluationCycleName;
    private Double averageScore;
}
