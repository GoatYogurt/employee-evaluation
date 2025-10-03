package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.EmployeeRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.EmployeeSearchDTO;
import com.vtit.intern.enums.Role;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.CriterionGroup;
import com.vtit.intern.models.Employee;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.services.EmployeeService;
import com.vtit.intern.utils.ResponseUtil;
import org.springframework.data.domain.Page;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private final EmployeeRepository repository;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository repository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> getById(Long id) {
        return ResponseEntity.ok(ResponseUtil.success(repository.findById(id)
                .map(employee -> modelMapper.map(employee, EmployeeResponseDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id))));
    }

    @Override
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> create(EmployeeRequestDTO dto) {
        if (repository.existsByUsername(dto.getUsername())) {
            throw new ResourceNotFoundException("Cannot create. Employee with username " + dto.getUsername() + " already exists.");
        }

        if (repository.existsByEmail(dto.getEmail())) {
            throw new ResourceNotFoundException("Cannot create. Employee with email " + dto.getEmail() + " already exists.");
        }

        if (repository.existsByStaffCode(dto.getStaffCode())) {
            throw new ResourceNotFoundException("Cannot create. Employee with staff code " + dto.getStaffCode() + " already exists.");
        }

        Employee employee = modelMapper.map(dto, Employee.class);

        // encode the password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        Employee savedEmployee = repository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.created(modelMapper.map(savedEmployee, EmployeeResponseDTO.class)));
    }

    @Override
    public void delete(Long id) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        existing.setDeleted(true);        // xóa mềm
        repository.save(existing);
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<EmployeeResponseDTO>>> getAllEmployees(EmployeeSearchDTO dto, Pageable pageable) {
        if (dto == null) {
            Page<Employee> employeePage = repository.findAll(pageable);
            List<EmployeeResponseDTO> content = employeePage.getContent().stream()
                    .map(e -> modelMapper.map(e, EmployeeResponseDTO.class))
                    .toList();

            return ResponseEntity.ok(ResponseUtil.success(new PageResponse<>(
                    content,
                    employeePage.getNumber(),
                    employeePage.getSize(),
                    employeePage.getTotalElements(),
                    employeePage.getTotalPages(),
                    employeePage.isLast()
            )));
        }

        String searchFullName = dto.getFullName() != null ? dto.getFullName().trim() : "";
        String searchDepartment = dto.getDepartment() != null ? dto.getDepartment().trim() : "";
        String searchRole = dto.getRole() != null ? dto.getRole().trim() : "";
        String searchUsername = dto.getUsername() != null ? dto.getUsername().trim() : "";
        String searchEmail = dto.getEmail() != null ? dto.getEmail().trim() : "";
        Integer searchStaffCode = null;

        String searchLevel = dto.getLevel() != null ? dto.getLevel().trim() : "";

        Page<Employee> employeePage = repository
                .searchEmployees(searchFullName, searchUsername, searchEmail, searchDepartment, searchRole, searchLevel, searchStaffCode, pageable);

        List<EmployeeResponseDTO> content = employeePage.getContent().stream()
                .peek(e -> {
                    e.setPassword(null); // Clear password before returning
                })
                .map(e -> modelMapper.map(e, EmployeeResponseDTO.class))
                .toList();

        return ResponseEntity.ok(ResponseUtil.success(new PageResponse<>(
                content,
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isLast()
        )));
    }

    @Override
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> patch(Long id, EmployeeRequestDTO dto) {
        Employee existingEmployee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (dto.getFullName() != null) {
            existingEmployee.setFullName(dto.getFullName());
        }
        if (dto.getDepartment() != null) {
            existingEmployee.setDepartment(dto.getDepartment());
        }
        if (dto.getRole() != null) {
            existingEmployee.setRole(Role.valueOf(dto.getRole()));
        }
        if (dto.getLevel() != null) {
            existingEmployee.setRole(Role.valueOf(dto.getLevel()));
        }
        if (dto.getEmail() != null) {
            existingEmployee.setEmail(dto.getEmail());
        }
        if (dto.getUsername() != null) {
            existingEmployee.setUsername(dto.getUsername());
        }
        if (dto.getStaffCode() != null) {
            existingEmployee.setStaffCode(dto.getStaffCode());
        }

        Employee updatedEmployee = repository.save(existingEmployee);
        updatedEmployee.setPassword(null); // Clear password before returning
        return ResponseEntity.ok(ResponseUtil.success(modelMapper.map(updatedEmployee, EmployeeResponseDTO.class)));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Employee employee = repository.findByUsername(username).
                orElseThrow(() -> new ResourceNotFoundException("Employee not found with username: " + username));

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty.");
        }

        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new ResourceNotFoundException("Old password is incorrect.");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        repository.save(employee);
    }
}
