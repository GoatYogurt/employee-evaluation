package com.vtit.intern.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name="project")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;

    private boolean isOdc;
    private boolean isDeleted = false;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Many-to-Many with Employee
    @ManyToMany
    @JoinTable(
            name = "project_employee",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "employee_id"})
    )
    private Set<Employee> employees = new HashSet<>();

    // Many-to-Many with EvaluationCycle
    @ManyToMany
    @JoinTable(
            name = "evaluation_cycle_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "evaluation_cycle_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "evaluation_cycle_id"})
    )
    private Set<EvaluationCycle> evaluationCycles = new HashSet<>();
}
