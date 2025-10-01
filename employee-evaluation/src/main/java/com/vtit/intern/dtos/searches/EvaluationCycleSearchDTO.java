package com.vtit.intern.dtos.searches;

import com.vtit.intern.enums.EvaluationCycleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationCycleSearchDTO {
    private String name;
    private String description;
    private EvaluationCycleStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
