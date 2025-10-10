package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.EmployeeRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.EmployeeSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface EmployeeService {
    ResponseEntity<ResponseDTO<EmployeeResponseDTO>> getById(Long id);
    ResponseEntity<ResponseDTO<EmployeeResponseDTO>> create(EmployeeRequestDTO dto);
//    EmployeeResponseDTO update(Long id, EmployeeRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> delete(Long id);

    ResponseEntity<ResponseDTO<PageResponse<EmployeeResponseDTO>>> getAllEmployees(EmployeeSearchDTO dto, Pageable pageable);

    ResponseEntity<ResponseDTO<EmployeeResponseDTO>> patch(Long id, EmployeeRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> changePassword(String username, String oldPassword, String newPassword);
}
