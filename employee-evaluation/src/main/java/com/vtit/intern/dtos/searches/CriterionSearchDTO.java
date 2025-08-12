package com.vtit.intern.dtos.searches;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CriterionSearchDTO {
    private String name;
    private String description;
    private Double minWeight;
    private Double maxWeight;
}
