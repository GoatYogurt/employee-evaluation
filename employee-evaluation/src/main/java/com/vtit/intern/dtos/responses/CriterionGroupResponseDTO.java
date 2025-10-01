package com.vtit.intern.dtos.responses;

import com.vtit.intern.models.Criterion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CriterionGroupResponseDTO {
    private String name;
    private String description;
    private Criterion criteria;
}
