package com.vtit.intern.dtos.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ProjectResponseDTO {
    private Long id;
    private String code;
    private Boolean isOdc;
    private String managerName;
    private Set<EmployeeResponseDTO> employees;
    private Set<Long> evaluationCycleIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}