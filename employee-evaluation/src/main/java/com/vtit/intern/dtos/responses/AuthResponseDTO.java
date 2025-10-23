package com.vtit.intern.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;

    private String tokenType = "Bearer";

    private String username;
    private String role;
    private String department;
    private String email;
    private String fullName;
    private String level;
    private Integer staffCode;

    public AuthResponseDTO(String accessToken, String refreshToken, String username, String role, String department, String email, String fullName, String level, Integer staffCode) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
        this.department = department;
        this.email = email;
        this.fullName = fullName;
        this.level = level;
        this.staffCode = staffCode;
    }
}
