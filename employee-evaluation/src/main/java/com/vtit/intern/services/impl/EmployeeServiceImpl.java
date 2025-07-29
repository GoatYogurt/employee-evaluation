package com.vtit.intern.services.impl;

import com.vtit.intern.EmployeeEvaluationApplication;
import com.vtit.intern.dtos.EmployeeDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Employee;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.services.EmployeeService;
import org.springframework.data.domain.Page;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private final EmployeeRepository repository;
    @Autowired
    private final ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public EmployeeDTO getById(Long id) {
        return repository.findById(id)
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public EmployeeDTO create(EmployeeDTO employeeDto) {
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        Employee savedEmployee = repository.save(employee);
        return modelMapper.map(savedEmployee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO update(Long id, EmployeeDTO employeeDTO) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot update. Employee not found with id: " + id);
        }

        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employee.setId(id);
        Employee updatedEmployee = repository.save(employee);
        return modelMapper.map(updatedEmployee, EmployeeDTO.class);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Employee not found with id: " + id);
        }

        repository.deleteById(id);
    }

    @Override
    public PageResponse<EmployeeDTO> getAllEmployees(String name, String department, String position, String role, Double salaryMin, Double salaryMax, Pageable pageable) {
        String searchName = name != null ? name.trim() : "";
        String searchDepartment = department != null ? department.trim() : "";
        String searchPosition = position != null ? position.trim() : "";
        String searchRole = role != null ? role.trim() : "";
        Double searchSalaryMin = salaryMin != null ? salaryMin : 0.0;
        Double searchSalaryMax = salaryMax != null ? salaryMax : Double.MAX_VALUE;


        Page<Employee> employeePage = repository
                .searchEmployees(searchName, searchDepartment, searchPosition, searchRole, searchSalaryMin, searchSalaryMax, pageable);

        List<EmployeeDTO> content = employeePage.getContent().stream()
                .map(e -> modelMapper.map(e, EmployeeDTO.class))
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
    public EmployeeDTO patch(Long id, EmployeeDTO employeeDto) {
        Employee existingEmployee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (employeeDto.getName() != null) {
            existingEmployee.setName(employeeDto.getName());
        }
        if (employeeDto.getDepartment() != null) {
            existingEmployee.setDepartment(employeeDto.getDepartment());
        }
        if (employeeDto.getPosition() != null) {
            existingEmployee.setPosition(employeeDto.getPosition());
        }
        if (employeeDto.getRole() != null) {
            existingEmployee.setRole(employeeDto.getRole());
        }
        if (employeeDto.getSalary() != null) {
            existingEmployee.setSalary(employeeDto.getSalary());
        }

        Employee updatedEmployee = repository.save(existingEmployee);
        return modelMapper.map(updatedEmployee, EmployeeDTO.class);
    }
}
