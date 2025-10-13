package com.vtit.intern.services.impl;

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

import java.util.List;
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
            throw new ResourceNotFoundException("Project with code " + dto.getCode() + " already exists.");
        }

        Employee manager = employeeRepository.findByIdAndIsDeletedFalse(dto.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + dto.getManagerId()));

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
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
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
        Project existing = projectRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (dto.getCode() != null) existing.setCode(dto.getCode());
        if (dto.getManagerId() != null) {
            Employee manager = employeeRepository.findByIdAndIsDeletedFalse(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + dto.getManagerId()));
            existing.setManager(manager);
        }
        if (dto.getIsOdc() != null) existing.setOdc(dto.getIsOdc());

        Project updated = projectRepository.save(existing);
        return ResponseUtil.success(toResponseDTO(updated));
    }

    public ResponseEntity<ResponseDTO<Void>> delete(Long id) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("CriterionGroup not found with id: " + id));

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
        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        EvaluationCycle evaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(evaluationCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation cycle not found with id: " + evaluationCycleId));
        evaluationCycle.getProjects().add(project);
        evaluationCycleRepository.save(evaluationCycle);
        return ResponseUtil.success("Project added to evaluation cycle successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> removeProjectFromEvaluationCycle(Long projectId, Long evaluationCycleId) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        EvaluationCycle evaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(evaluationCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation cycle not found with id: " + evaluationCycleId));
        evaluationCycle.getProjects().remove(project);
        evaluationCycleRepository.save(evaluationCycle);
        return ResponseUtil.success("Project removed from evaluation cycle successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> addEmployeeToProject(Long projectId, Long employeeId) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        Set<EvaluationCycle> evaluationCycles = project.getEvaluationCycles();
        for (EvaluationCycle cycle : evaluationCycles) {
            Evaluation evaluation = new Evaluation();
            evaluation.setEmployee(employee);
            evaluation.setEvaluationCycle(cycle);
            evaluation.setDeleted(false);
            evaluation.setProject(project);
            evaluation.setTotalScore(0.0);
            evaluationRepository.save(evaluation);
        }

        project.getEmployees().add(employee);
        projectRepository.save(project);
        return ResponseUtil.success("Employee added to project successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> removeEmployeeFromProject(Long projectId, Long employeeId) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        project.getEmployees().remove(employee);
        projectRepository.save(project);
        return ResponseUtil.success("Employee removed from project successfully.");
    }

    public ProjectResponseDTO toResponseDTO(Project project) {
        ProjectResponseDTO dto = modelMapper.map(project, ProjectResponseDTO.class);
        dto.setManagerName(project.getManager().getFullName());
        dto.setEmployees(project.getEmployees().stream().map(emp -> modelMapper.map(emp, EmployeeResponseDTO.class)).collect(java.util.stream.Collectors.toSet()));
        dto.setEvaluationCycleIds(project.getEvaluationCycles().stream().map(EvaluationCycle::getId).collect(java.util.stream.Collectors.toSet()));
        return dto;
    }
}
