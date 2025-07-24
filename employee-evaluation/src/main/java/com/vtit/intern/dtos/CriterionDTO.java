package com.vtit.intern.dtos;

import lombok.Data;

@Data
public class CriterionDTO {
    private Long id;
    private String name;
    private String description;
    private Double weight;
}
