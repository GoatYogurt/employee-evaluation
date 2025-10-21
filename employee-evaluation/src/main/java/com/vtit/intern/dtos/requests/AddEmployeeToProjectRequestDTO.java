package com.vtit.intern.dtos.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddEmployeeToProjectRequestDTO {
    @NotBlank
    private Long employeeId;

    @NotBlank
    private Long evaluationCycleId;

    @NotBlank
    private Long projectId;
}
