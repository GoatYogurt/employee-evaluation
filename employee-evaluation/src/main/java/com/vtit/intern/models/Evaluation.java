package com.vtit.intern.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne @JoinColumn(name = "evaluation_cycle_id", nullable = false)
    private EvaluationCycle evaluationCycle;

    @ManyToOne @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private double totalScore;
    private String completionLevel;
    private String kiRanking;
    private String managerFeedback;
    private String customerFeedback;
    private String note;

    private boolean isDeleted = false;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluationScore> scores = new HashSet<>();

    /**
     * Set total score and update completion level and KI ranking
     */
    public void setTotalScore(double totalScore) {
        if (totalScore < 0 || totalScore > 5) {
            throw new IllegalArgumentException("Tổng điểm phải nằm trong khoảng từ 0 đến 5");
        }

        totalScore = Math.round(totalScore * 100.0) / 100.0;

        this.totalScore = totalScore;
        this.completionLevel = scoreToCompletionLevel(totalScore);
        this.kiRanking = scoreToRanking(totalScore);
    }

    // total score to KI ranking
    private String scoreToRanking(Double totalScore) {
        if (totalScore.isNaN())
            return "N/A";

        if (totalScore < 0 || totalScore > 5) {
            throw new IllegalArgumentException("Tổng điểm phải nằm trong khoảng từ 0 đến 5");
        }

        if (totalScore >= 4.5) {
            return "A+";
        } else if (totalScore >= 3.5) {
            return "A";
        } else if (totalScore >= 2.5) {
            return "B";
        } else if (totalScore >= 2) {
            return "C";
        } else {
            return "D";
        }
    }

    private String scoreToCompletionLevel(Double totalScore) {
        if (totalScore.isNaN())
            return "N/A";

        if (totalScore < 0 || totalScore > 5) {
            throw new IllegalArgumentException("Tổng điểm phải nằm trong khoảng từ 0 đến 5");
        }

        if (totalScore >= 4.5) {
            return "Vượt xa yêu cầu";
        } else if (totalScore >= 3.5) {
            return "Vượt yêu cầu";
        } else if (totalScore >= 2.5) {
            return "Đạt yêu cầu";
        } else if (totalScore >= 2) {
            return "Gần đạt yêu cầu";
        } else {
            return "Không đạt yêu cầu";
        }
    }
}

