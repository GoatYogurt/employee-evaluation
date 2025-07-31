package com.vtit.intern.controllers;

import com.vtit.intern.dtos.*;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Employee;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.requests.ChangePasswordRequestDTO;
import com.vtit.intern.requests.LoginRequestDTO;
import com.vtit.intern.requests.RefreshTokenRequestDTO;
import com.vtit.intern.responses.AuthResponseDTO;
import com.vtit.intern.services.impl.EmployeeServiceImpl;
import com.vtit.intern.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    private final EmployeeRepository employeeRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, EmployeeServiceImpl employeeServiceImpl, EmployeeRepository employeeRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.employeeServiceImpl = employeeServiceImpl;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Employee employee = employeeRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + request.getUsername()));

            return new AuthResponseDTO(jwtUtil.generateAccessToken(employee),
                    jwtUtil.generateRefreshToken(employee),
                    request.getUsername());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password", e);
        }
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody EmployeeDTO employeeDTO) {
        employeeServiceImpl.create(employeeDTO);
        Employee employee = employeeRepository.findByUsername(employeeDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + employeeDTO.getUsername()));

        return new AuthResponseDTO(jwtUtil.generateAccessToken(employee),
                jwtUtil.generateRefreshToken(employee),
                employeeDTO.getUsername());
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refresh(@RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtUtil.generateAccessToken(employee);

        return new AuthResponseDTO(newAccessToken, refreshToken, username);
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
