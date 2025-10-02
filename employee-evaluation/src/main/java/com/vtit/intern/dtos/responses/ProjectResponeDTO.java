package com.vtit.intern.dtos.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectResponeDTO {
    private Long id;
    private String code;
    private Boolean isOdc;
    private String managerName;
    private Set<String> employeeNames;
}