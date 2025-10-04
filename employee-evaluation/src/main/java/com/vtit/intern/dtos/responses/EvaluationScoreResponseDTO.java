package com.vtit.intern.dtos.responses;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationScoreResponseDTO {
    private Long id;
    private Long criterionId;
    private String criterionName;
    private Long evaluationId;
    private double score;
}
