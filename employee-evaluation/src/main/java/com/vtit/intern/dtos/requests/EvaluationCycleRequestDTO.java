package com.vtit.intern.dtos.requests;

import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.models.EvaluationCycleStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCycleRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    @NotNull(message = "Status cannot be null")
    private EvaluationCycleStatus status;

    @Valid
    private Set<EvaluationResponseDTO> evaluations;

    @NotNull(message = "Employees cannot be null")
    @Size(min = 1, message = "At least one employee must be selected")
    private Set<Long> employees;

    private Set<Long> managers;
}
