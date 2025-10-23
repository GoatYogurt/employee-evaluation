package com.vtit.intern.dtos.responses;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.math.BigDecimal;
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

    public double getTotalScore() {
        return BigDecimal.valueOf(totalScore)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}