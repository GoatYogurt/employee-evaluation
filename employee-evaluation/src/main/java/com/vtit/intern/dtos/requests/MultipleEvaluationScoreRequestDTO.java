package com.vtit.intern.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
public class MultipleEvaluationScoreRequestDTO {
    private Long evaluationId;
    private List<CriterionScoreRequestDTO> scores;
}
