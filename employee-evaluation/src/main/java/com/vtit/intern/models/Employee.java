package com.vtit.intern.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String username;
    private String password;
    private String email;

    private String position;
    private String department;
    private double salary;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "employee")
    private List<Evaluation> evaluations = new ArrayList<>();
}
