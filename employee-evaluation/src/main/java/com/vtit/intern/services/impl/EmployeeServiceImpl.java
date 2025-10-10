package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.EmployeeRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.EmployeeSearchDTO;
import com.vtit.intern.enums.Level;
import com.vtit.intern.enums.Role;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Employee;
import com.vtit.intern.models.Project;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.repositories.ProjectRepository;
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
    private final EmployeeRepository employeeRepository;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final ProjectRepository projectRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ProjectRepository projectRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> getById(Long id) {
        return ResponseEntity.ok(ResponseUtil.success(employeeRepository.findById(id)
                .map(employee -> modelMapper.map(employee, EmployeeResponseDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id))));
    }

    @Override
    public ResponseEntity<ResponseDTO<EmployeeResponseDTO>> create(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByUsername(dto.getUsername())) {
            throw new ResourceNotFoundException("Cannot create. Employee with username " + dto.getUsername() + " already exists.");
        }

        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceNotFoundException("Cannot create. Employee with email " + dto.getEmail() + " already exists.");
        }

        if (employeeRepository.existsByStaffCode(dto.getStaffCode())) {
            throw new ResourceNotFoundException("Cannot create. Employee with staff code " + dto.getStaffCode() + " already exists.");
        }

        Employee employee = modelMapper.map(dto, Employee.class);

        // encode the password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        Employee savedEmployee = employeeRepository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.created(modelMapper.map(savedEmployee, EmployeeResponseDTO.class)));
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setDeleted(true);
        Page<Project> projects = projectRepository.findByIdIn(employee.getProjects().stream().map(Project::getId).toList(), null);
        projects.forEach(project -> project.getEmployees().remove(employee));
        employeeRepository.save(employee);
        return ResponseEntity.ok(ResponseUtil.deleted());
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<EmployeeResponseDTO>>> getAllEmployees(EmployeeSearchDTO dto, Pageable pageable) {
        if (dto == null) {
            Page<Employee> employeePage = employeeRepository.findAll(pageable);
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

        Page<Employee> employeePage = employeeRepository
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
        Employee existingEmployee = employeeRepository.findById(id)
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
            existingEmployee.setLevel(Level.valueOf(dto.getLevel()));
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

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        updatedEmployee.setPassword(null); // Clear password before returning
        return ResponseEntity.ok(ResponseUtil.success(modelMapper.map(updatedEmployee, EmployeeResponseDTO.class)));
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> changePassword(String username, String oldPassword, String newPassword) {
        Employee employee = employeeRepository.findByUsername(username).
                orElseThrow(() -> new ResourceNotFoundException("Employee not found with username: " + username));

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty.");
        }

        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new ResourceNotFoundException("Old password is incorrect.");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);

        return ResponseEntity.ok(ResponseUtil.success("Password changed successfully"));
    }
}
