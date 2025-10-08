package com.vtit.intern.dtos.searches;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class EvaluationScoreSearchDTO {
    private Long criterionId;
    private String criterionName;
    private Long evaluationId;
    private double score;
}
