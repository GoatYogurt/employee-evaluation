package com.vtit.intern.dtos.searches;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectSearchDTO {
    private String code;
    private String managerName;
    private Boolean isOdc;
}