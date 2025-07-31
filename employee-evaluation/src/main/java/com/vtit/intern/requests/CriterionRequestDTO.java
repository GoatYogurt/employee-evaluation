package com.vtit.intern.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriterionRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Weight cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be greater than 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Weight must be less than or equal to 1")
    private Double weight;
}
