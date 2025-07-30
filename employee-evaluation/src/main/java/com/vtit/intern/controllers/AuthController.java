package com.vtit.intern.controllers;

import com.vtit.intern.dtos.AuthResponseDTO;
import com.vtit.intern.dtos.ChangePasswordRequestDTO;
import com.vtit.intern.dtos.EmployeeDTO;
import com.vtit.intern.dtos.LoginRequestDTO;
import com.vtit.intern.services.impl.EmployeeServiceImpl;
import com.vtit.intern.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeServiceImpl employeeServiceImpl;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, EmployeeServiceImpl employeeServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.employeeServiceImpl = employeeServiceImpl;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(
            @RequestBody LoginRequestDTO request
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            return new AuthResponseDTO(jwtUtil.generateToken(request.getUsername()), request.getUsername());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password", e);
        }
    }

    @PostMapping("/register")
    public AuthResponseDTO register(
            @RequestBody EmployeeDTO employeeDTO
    ) {
        employeeServiceImpl.create(employeeDTO);
        return new AuthResponseDTO(jwtUtil.generateToken(employeeDTO.getUsername()), employeeDTO.getUsername());
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequestDTO changePasswordRequest,
            Authentication authentication
    ) {
        String username = authentication.getName();
        System.out.println("Changing password for user: " + username);
        employeeServiceImpl.changePassword(username, changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }
}
