package com.vtit.intern.dtos.requests;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project code cannot be blank")
    @Size(max = 100, message = "Project code cannot exceed 100 characters")
    private String code;

    @NotNull(message = "Manager ID cannot be null")
    private Long managerId;

    private Boolean isOdc;

    private Boolean isDeleted = false;
    private Set<Long> employeeIds;
    private Set<Long> evaluationCycleIds;
}
