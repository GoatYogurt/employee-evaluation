package com.vtit.intern.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestDTO {
    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    @NotNull(message = "Evaluation cycle ID cannot be null")
    private Long evaluationCycleId;

    @NotNull(message = "Project ID cannot be null")
    private Long projectId;

    private String managerFeedback;

    private String customerFeedback;

    private String note;

    private boolean isDeleted = false;
}
