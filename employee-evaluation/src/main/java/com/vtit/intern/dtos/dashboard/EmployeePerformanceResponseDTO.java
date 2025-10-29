package com.vtit.intern.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EmployeePerformanceResponseDTO {
    private Long employeeId;
    private String employeeName;
    private Double averageScore;
}
