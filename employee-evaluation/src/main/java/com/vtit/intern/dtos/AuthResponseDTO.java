package com.vtit.intern.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vtit.intern.utils.JwtUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken = "Not yet implemented";

    private String tokenType = "Bearer";
    private String username;

    public AuthResponseDTO(String accessToken, String username) {
        this.accessToken = accessToken;
        this.username = username;
    }
}
