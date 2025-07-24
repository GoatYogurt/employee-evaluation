package com.vtit.intern.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {
    private Long employeeId;
    private Long criterionId;
    private Double score;
    private String comment;
    private LocalDate evaluationDate;
}
