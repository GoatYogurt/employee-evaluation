package com.vtit.intern.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private EvaluationCycleStatus status;

    @OneToMany(mappedBy = "evaluationCycle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

    @ManyToMany
    @JoinTable(name = "evaluation_cycle_employee",
            joinColumns = @JoinColumn(name = "evaluation_cycle_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "evaluation_cycle_manager",
            joinColumns = @JoinColumn(name = "evaluation_cycle_id"),
            inverseJoinColumns = @JoinColumn(name = "manager_id"))
    private Set<Employee> managers = new HashSet<>();
}
