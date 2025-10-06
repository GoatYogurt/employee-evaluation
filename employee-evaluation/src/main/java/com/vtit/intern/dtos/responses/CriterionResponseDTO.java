package com.vtit.intern.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vtit.intern.models.CriterionGroup;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CriterionResponseDTO {
    private String name;
    private String description;
    private Double weight;
    private Long groupId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
