package com.vtit.intern.dtos.responses;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponseDTO {
    private Long id;
    private Long employeeId;
    private Long evaluationCycleId;
    private Long projectId;
    private double totalScore;
    private String completionLevel;
    private String kiRanking;
    private String managerFeedback;
    private String customerFeedback;
    private String note;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
