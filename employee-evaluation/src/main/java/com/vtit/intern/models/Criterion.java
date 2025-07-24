package com.vtit.intern.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Criterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double weight;

    @OneToMany(mappedBy = "criterion")
    private List<Evaluation> evaluations = new ArrayList<>();
}
