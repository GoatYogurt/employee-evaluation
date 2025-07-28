package com.vtit.intern.services.impl;

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
    public PageResponse<EmployeeDTO> getAllEmployees(Pageable pageable) {
        Page<Employee> employeePage = repository.findAll(pageable);

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
}
