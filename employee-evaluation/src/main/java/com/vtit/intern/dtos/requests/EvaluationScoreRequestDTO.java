package com.vtit.intern.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationScoreRequestDTO {
    private Long criterionId;
    private Long evaluationId;
    private double score;
    private boolean isDeleted = false;

}
