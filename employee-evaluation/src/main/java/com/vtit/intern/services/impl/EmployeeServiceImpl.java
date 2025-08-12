package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.EmployeeRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.searches.EmployeeSearchDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Employee;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.services.EmployeeService;
import org.springframework.data.domain.Page;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    public EmployeeResponseDTO getById(Long id) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setPassword(null); // Clear password before returning
                    return employee;
                })
                .map(employee -> modelMapper.map(employee, EmployeeResponseDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public EmployeeResponseDTO create(EmployeeRequestDTO dto) {
        if (repository.existsByUsername(dto.getUsername())) {
            throw new ResourceNotFoundException("Cannot create. Employee with username " + dto.getUsername() + " already exists.");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new ResourceNotFoundException("Cannot create. Employee with email " + dto.getEmail() + " already exists.");
        }

        Employee employee = modelMapper.map(dto, Employee.class);

        // encode the password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        Employee savedEmployee = repository.save(employee);
        savedEmployee.setPassword(null); // Clear password before returning
        return modelMapper.map(savedEmployee, EmployeeResponseDTO.class);
    }

//    @Override
//    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO dto) {
//        if (!repository.existsById(id)) {
//            throw new ResourceNotFoundException("Cannot update. Employee not found with id: " + id);
//        }
//
//        Employee employee = modelMapper.map(dto, Employee.class);
//        employee.setId(id);
//        Employee updatedEmployee = repository.save(employee);
////        updatedEmployee.setPassword(null); // Clear password before returning
//        return modelMapper.map(updatedEmployee, EmployeeResponseDTO.class);
//    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Employee not found with id: " + id);
        }

        repository.deleteById(id);
    }

    @Override
    public PageResponse<EmployeeResponseDTO> getAllEmployees(String name, String username, String email, String department, String position, String role, Double salaryMin, Double salaryMax, Pageable pageable) {
        String searchName = name != null ? name.trim() : "";
        String searchDepartment = department != null ? department.trim() : "";
        String searchPosition = position != null ? position.trim() : "";
        String searchRole = role != null ? role.trim() : "";
        Double searchSalaryMin = salaryMin != null ? salaryMin : 0.0;
        Double searchSalaryMax = salaryMax != null ? salaryMax : Double.MAX_VALUE;
        String searchUsername = username != null ? username.trim() : "";
        String searchEmail = email != null ? email.trim() : "";


        Page<Employee> employeePage = repository
                .searchEmployees(searchName, searchUsername, searchEmail, searchDepartment, searchPosition, searchRole, searchSalaryMin, searchSalaryMax, pageable);

        List<EmployeeResponseDTO> content = employeePage.getContent().stream()
                .peek(e -> {
                    e.setPassword(null); // Clear password before returning
                })
                .map(e -> modelMapper.map(e, EmployeeResponseDTO.class))
                .toList();

        return new PageResponse<>(
                content,
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isLast()
        );
    }

    @Override
    public EmployeeResponseDTO patch(Long id, EmployeeRequestDTO dto) {
        Employee existingEmployee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (dto.getName() != null) {
            existingEmployee.setName(dto.getName());
        }
        if (dto.getDepartment() != null) {
            existingEmployee.setDepartment(dto.getDepartment());
        }
        if (dto.getPosition() != null) {
            existingEmployee.setPosition(dto.getPosition());
        }
        if (dto.getRole() != null) {
            existingEmployee.setRole(dto.getRole());
        }
        if (dto.getSalary() != null) {
            existingEmployee.setSalary(dto.getSalary());
        }
        if (dto.getEmail() != null) {
            existingEmployee.setEmail(dto.getEmail());
        }
        if (dto.getUsername() != null) {
            existingEmployee.setUsername(dto.getUsername());
        }

        Employee updatedEmployee = repository.save(existingEmployee);
        updatedEmployee.setPassword(null); // Clear password before returning
        return modelMapper.map(updatedEmployee, EmployeeResponseDTO.class);
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
