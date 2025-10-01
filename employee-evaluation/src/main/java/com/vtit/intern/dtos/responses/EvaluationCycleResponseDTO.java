package com.vtit.intern.dtos.responses;

import com.vtit.intern.enums.EvaluationCycleStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCycleResponseDTO {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private EvaluationCycleStatus status;
}
