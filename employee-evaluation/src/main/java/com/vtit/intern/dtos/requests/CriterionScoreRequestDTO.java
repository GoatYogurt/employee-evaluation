package com.vtit.intern.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CriterionScoreRequestDTO {
    private Long criterionId;
    private double score;
}