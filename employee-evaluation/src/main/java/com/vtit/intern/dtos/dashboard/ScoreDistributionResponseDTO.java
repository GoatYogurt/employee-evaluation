package com.vtit.intern.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScoreDistributionResponseDTO {
    private String range;
    private Long count;
}
