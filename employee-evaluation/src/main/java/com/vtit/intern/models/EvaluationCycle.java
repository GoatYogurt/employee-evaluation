package com.vtit.intern.models;

import com.vtit.intern.enums.EvaluationCycleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evaluation_cycle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EvaluationCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private boolean isDeleted = false;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private EvaluationCycleStatus status;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "evaluation_cycle_project",
            joinColumns = @JoinColumn(name = "evaluation_cycle_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"evaluation_cycle_id", "project_id"})
    )
    private Set<Project> projects = new HashSet<>();
}


