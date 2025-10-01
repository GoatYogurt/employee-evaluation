package com.vtit.intern.dtos.responses;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponseDTO {
    private Long employeeId;
    private Long evaluationCycleId;
    private Long projectId;
    private double totalScore;
    private String completionLevel;
    private String kiRanking;
    private String managerFeedback;
    private String customerFeedback;
    private String note;
}
