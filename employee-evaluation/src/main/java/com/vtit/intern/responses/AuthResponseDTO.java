package com.vtit.intern.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;

    private String tokenType = "Bearer";
    private String username;

    public AuthResponseDTO(String accessToken, String refreshToken, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
    }
}
