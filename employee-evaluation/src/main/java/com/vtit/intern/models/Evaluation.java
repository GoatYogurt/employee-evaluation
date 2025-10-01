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
}

