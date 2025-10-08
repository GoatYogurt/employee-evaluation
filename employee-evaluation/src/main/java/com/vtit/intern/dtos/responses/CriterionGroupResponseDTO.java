package com.vtit.intern.dtos.responses;

import com.vtit.intern.models.Criterion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CriterionGroupResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Set<CriterionResponseDTO> criteria;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
