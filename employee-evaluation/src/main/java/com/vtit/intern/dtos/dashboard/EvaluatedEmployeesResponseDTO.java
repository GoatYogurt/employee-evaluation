package com.vtit.intern.dtos.dashboard;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EvaluatedEmployeesResponseDTO {
    private String cycleName;
    private int evaluatedCount;
    private int notEvaluatedCount;
    private int totalCount;
}
