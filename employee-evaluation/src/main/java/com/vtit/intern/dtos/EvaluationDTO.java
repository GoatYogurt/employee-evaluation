package com.vtit.intern.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    @NotNull(message = "Criterion ID cannot be null")
    private Long criterionId;

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Score must be greater than 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Score must be less than or equal to 10")
    private Double score;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    @PastOrPresent(message = "Evaluation date cannot be in the future")
    private LocalDate evaluationDate;

    @NotNull(message = "Evaluation cycle ID cannot be null")
    private Long evaluationCycleId;
}
