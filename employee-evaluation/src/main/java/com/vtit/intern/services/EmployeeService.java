package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EmployeeRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeResponseDTO getById(Long id);
    EmployeeResponseDTO create(EmployeeRequestDTO dto);
//    EmployeeResponseDTO update(Long id, EmployeeRequestDTO dto);
    void delete(Long id);

    PageResponse<EmployeeResponseDTO> getAllEmployees(String name, String username, String email, String department, String position, String role, Double salaryMin, Double salaryMax, Pageable pageable);

    EmployeeResponseDTO patch(Long id, EmployeeRequestDTO dto);
    void changePassword(String username, String oldPassword, String newPassword);
}
