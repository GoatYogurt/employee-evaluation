package com.vtit.intern.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "criterion_id")
    private Criterion criterion;

    private Double score;
    private String comment;
    private LocalDate evaluationDate;

    @ManyToOne
    @JoinColumn(name = "evaluation_cycle_id")
    private EvaluationCycle evaluationCycle;

//    public void setEvaluationCycle(EvaluationCycle evaluationCycle) {
//        if (this.evaluationCycle != null) {
//            this.evaluationCycle.removeEvaluation(this);
//        }
//        this.evaluationCycle = evaluationCycle;
//        if (evaluationCycle != null) {
//            evaluationCycle.addEvaluation(this);
//        }
//    }
//
//    public void removeEvaluationCycle() {
//        if (this.evaluationCycle != null) {
//            this.evaluationCycle.removeEvaluation(this);
//            this.evaluationCycle = null;
//        }
//    }
}
