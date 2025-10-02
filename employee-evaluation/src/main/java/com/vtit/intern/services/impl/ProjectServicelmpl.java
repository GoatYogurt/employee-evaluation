package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.ProjectRequestDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponeDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.ProjectSearchDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.CriterionGroup;
import com.vtit.intern.models.Employee;
import com.vtit.intern.models.Project;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.repositories.ProjectRepository;
import com.vtit.intern.services.ProjectService;
import com.vtit.intern.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ProjectServicelmpl implements ProjectService {
    @Autowired
    private final ProjectRepository projectRepository;
    @Autowired
    private final EmployeeRepository employeeRepository;
    @Autowired
    private final ModelMapper modelMapper;

    public ProjectServicelmpl(ProjectRepository projectRepository, EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<ResponseDTO<ProjectResponeDTO>> create(ProjectRequestDTO dto) {
        if (projectRepository.existsByCode(dto.getCode())) {
            throw new ResourceNotFoundException("Project with code " + dto.getCode() + " already exists.");
        }

        Employee manager = employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + dto.getManagerId()));

        Project project = new Project();
        project.setCode(dto.getCode());
        project.setManager(manager);
        project.setOdc(dto.getIsOdc());
        project.setDeleted(false);

        Project saved = projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.created(toDTO(saved)));
    }

    @Override
    public ResponseEntity<ResponseDTO<ProjectResponeDTO>> getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return ResponseEntity.ok(ResponseUtil.success(toDTO(project)));
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<ProjectResponeDTO>>> getAll(ProjectSearchDTO searchDTO, Pageable pageable) {
        if (searchDTO == null) searchDTO = new ProjectSearchDTO();

        Page<Project> page = projectRepository.searchProjects(
                searchDTO.getCode(),
                searchDTO.getManagerName(),
                searchDTO.getIsOdc(),
                pageable
        );

        var content = page.getContent().stream().map(this::toDTO).toList();

        return ResponseEntity.ok(ResponseUtil.success(new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        )));
    }

    @Override
    public ResponseEntity<ResponseDTO<ProjectResponeDTO>> patch(Long id, ProjectRequestDTO dto) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (dto.getCode() != null) existing.setCode(dto.getCode());
        if (dto.getManagerId() != null) {
            Employee manager = employeeRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + dto.getManagerId()));
            existing.setManager(manager);
        }
        if (dto.getIsOdc() != null) existing.setOdc(dto.getIsOdc());

        Project updated = projectRepository.save(existing);
        return ResponseEntity.ok(ResponseUtil.success(toDTO(updated)));
    }

    public void delete(Long id) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CriterionGroup not found with id: " + id));
        existing.setDeleted(true);        // xóa mềm
        projectRepository.save(existing);
    }

    private ProjectResponeDTO toDTO(Project project) {
        ProjectResponeDTO dto = modelMapper.map(project, ProjectResponeDTO.class);
        dto.setManagerName(project.getManager().getFullName());
        dto.setEmployeeNames(project.getEmployees()
                .stream().map(Employee::getFullName).collect(Collectors.toSet()));
        return dto;
    }
}
