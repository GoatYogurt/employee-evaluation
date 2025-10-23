package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.AddEmployeeToProjectRequestDTO;
import com.vtit.intern.dtos.requests.ProjectRequestDTO;
import com.vtit.intern.dtos.responses.EmployeeResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.ProjectSearchDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Employee;
import com.vtit.intern.models.Evaluation;
import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.models.Project;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.repositories.EvaluationCycleRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.repositories.ProjectRepository;
import com.vtit.intern.services.ProjectService;
import com.vtit.intern.utils.ResponseUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final EvaluationCycleRepository evaluationCycleRepository;
    private final EvaluationRepository evaluationRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseDTO<ProjectResponseDTO>> create(ProjectRequestDTO dto) {
        if (projectRepository.existsByCodeAndIsDeletedFalse(dto.getCode())) {
            return ResponseUtil.alreadyExists("Project with code " + dto.getCode() + " already exists.");
        }

        Optional<Employee> optionalManager = employeeRepository.findByIdAndIsDeletedFalse(dto.getManagerId());
        if (optionalManager.isEmpty()) {
            return ResponseUtil.notFound("Manager not found with id: " + dto.getManagerId());
        }
        Employee manager = optionalManager.get();

        Project project = new Project();
        project.setCode(dto.getCode());
        project.setManager(manager);
        project.setOdc(dto.getIsOdc());
        project.setDeleted(false);

        Set<Employee> employees = employeeRepository.findByIdInAndIsDeletedFalse(dto.getEmployeeIds());
        project.getEmployees().addAll(employees);

        Set<EvaluationCycle> evaluationCycles = evaluationCycleRepository.findByIdInAndIsDeletedFalse(dto.getEvaluationCycleIds());
        for (EvaluationCycle cycle : evaluationCycles) {
            cycle.getProjects().add(project);
        }

        Project saved = projectRepository.save(project);
        return ResponseUtil.created(toResponseDTO(saved));
    }

    @Override
    public ResponseEntity<ResponseDTO<ProjectResponseDTO>> getById(Long id) {
        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(id);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + id);
        }
        Project project = optionalProject.get();
        return ResponseUtil.success(toResponseDTO(project));
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<ProjectResponseDTO>>> getAll(ProjectSearchDTO searchDTO, Pageable pageable) {
        if (searchDTO == null) searchDTO = new ProjectSearchDTO();

        Page<Project> page = projectRepository.searchProjects(
                searchDTO.getCode(),
                searchDTO.getManagerName(),
                searchDTO.getIsOdc(),
                pageable
        );

        var content = page.getContent().stream().map(this::toResponseDTO).toList();

        return ResponseUtil.success(new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        ));
    }

    @Override
    public ResponseEntity<ResponseDTO<ProjectResponseDTO>> patch(Long id, ProjectRequestDTO dto) {
        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(id);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + id);
        }
        Project project = optionalProject.get();

        if (dto.getCode() != null) project.setCode(dto.getCode());
        if (dto.getManagerId() != null) {
            Optional<Employee> optionalManager = employeeRepository.findByIdAndIsDeletedFalse(dto.getManagerId());
            if (optionalManager.isEmpty()) {
                return ResponseUtil.notFound("Manager not found with id: " + dto.getManagerId());
            }
            Employee manager = optionalManager.get();
            project.setManager(manager);
        }
        if (dto.getIsOdc() != null) project.setOdc(dto.getIsOdc());

        Project updated = projectRepository.save(project);
        return ResponseUtil.success(toResponseDTO(updated));
    }

    public ResponseEntity<ResponseDTO<Void>> delete(Long id) {
        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(id);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + id);
        }
        Project project = optionalProject.get();
        // Remove project from associated evaluation cycles
        for (EvaluationCycle cycle: project.getEvaluationCycles()) {
            cycle.getProjects().remove(project);
        }
        evaluationCycleRepository.saveAll(project.getEvaluationCycles());

        project.getEmployees().clear();
        project.setDeleted(true);
        projectRepository.save(project);

        return ResponseUtil.deleted("Project " + project.getCode() + " deleted successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> addProjectToEvaluationCycle(Long projectId, Long evaluationCycleId) {
        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + projectId);
        }
        Project project = optionalProject.get();

        Optional<EvaluationCycle> optionalEvaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(evaluationCycleId);
        if (optionalEvaluationCycle.isEmpty()) {
            return ResponseUtil.notFound("Evaluation cycle not found with id: " + evaluationCycleId);
        }
        EvaluationCycle evaluationCycle = optionalEvaluationCycle.get();

        evaluationCycle.getProjects().add(project);
        evaluationCycleRepository.save(evaluationCycle);
        return ResponseUtil.success("Project added to evaluation cycle successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> removeProjectFromEvaluationCycle(Long projectId, Long evaluationCycleId) {
        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + projectId);
        }
        Project project = optionalProject.get();

        Optional<EvaluationCycle> optionalEvaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(evaluationCycleId);
        if (optionalEvaluationCycle.isEmpty()) {
            return ResponseUtil.notFound("Evaluation cycle not found with id: " + evaluationCycleId);
        }
        EvaluationCycle evaluationCycle = optionalEvaluationCycle.get();

        evaluationCycle.getProjects().remove(project);
        evaluationCycleRepository.save(evaluationCycle);
        return ResponseUtil.success("Project removed from evaluation cycle successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> addEmployeeToProject(AddEmployeeToProjectRequestDTO dto) {
        Long projectId = dto.getProjectId();
        Long employeeId = dto.getEmployeeId();

        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + projectId);
        }
        Project project = optionalProject.get();

        Optional<Employee> optionalEmployee = employeeRepository.findByIdAndIsDeletedFalse(employeeId);
        if (optionalEmployee.isEmpty()) {
            return ResponseUtil.notFound("Employee not found with id: " + employeeId);
        }
        Employee employee = optionalEmployee.get();

        project.getEmployees().add(employee);
        projectRepository.save(project);
        return ResponseUtil.success("Employee " + employee.getFullName() + " added to project " + project.getCode() + "successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> removeEmployeeFromProject(AddEmployeeToProjectRequestDTO dto) {
        Long projectId = dto.getProjectId();
        Long employeeId = dto.getEmployeeId();

        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + projectId);
        }
        Project project = optionalProject.get();

        Optional<Employee> optionalEmployee = employeeRepository.findByIdAndIsDeletedFalse(employeeId);
        if (optionalEmployee.isEmpty()) {
            return ResponseUtil.notFound("Employee not found with id: " + employeeId);
        }
        Employee employee = optionalEmployee.get();

        project.getEmployees().remove(employee);
        projectRepository.save(project);
        return ResponseUtil.success("Employee removed from project successfully. Also removed associated evaluations.");
    }

    public ProjectResponseDTO toResponseDTO(Project project) {
        ProjectResponseDTO dto = modelMapper.map(project, ProjectResponseDTO.class);
        dto.setManagerName(project.getManager().getFullName());
        dto.setEmployees(project.getEmployees().stream().map(emp -> modelMapper.map(emp, EmployeeResponseDTO.class)).collect(java.util.stream.Collectors.toSet()));
        dto.setEvaluationCycleIds(project.getEvaluationCycles().stream().map(EvaluationCycle::getId).collect(java.util.stream.Collectors.toSet()));
        return dto;
    }
}
