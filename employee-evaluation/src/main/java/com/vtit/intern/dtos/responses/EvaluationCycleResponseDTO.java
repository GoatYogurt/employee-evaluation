package com.vtit.intern.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vtit.intern.enums.EvaluationCycleStatus;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCycleResponseDTO {
    private Long id;
    private String name;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    private EvaluationCycleStatus status;

    private Set<Long> projectIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
