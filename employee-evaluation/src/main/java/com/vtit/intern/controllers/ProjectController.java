package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.AddEmployeeToProjectRequestDTO;
import com.vtit.intern.dtos.requests.ProjectRequestDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.ProjectSearchDTO;
import com.vtit.intern.services.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ResponseDTO<PageResponse<ProjectResponseDTO>>> getAll(
            @RequestBody(required = false) ProjectSearchDTO dto,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return projectService.getAll(dto, PageRequest.of(page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProjectResponseDTO>> getById(
            @PathVariable @Positive Long id) {
        return projectService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ProjectResponseDTO>> create(@Valid @RequestBody ProjectRequestDTO dto) {
        return projectService.create(dto);
    }

    @PutMapping("/{id}/add-evaluation-cycle/{cycleId}")
    public ResponseEntity<ResponseDTO<Void>> addProjectToEvaluationCycle(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long cycleId) {
        return projectService.addProjectToEvaluationCycle(id, cycleId);
    }

    @PutMapping("/{id}/remove-evaluation-cycle/{cycleId}")
    public ResponseEntity<ResponseDTO<Void>> removeProjectFromEvaluationCycle(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long cycleId) {
        return projectService.removeProjectFromEvaluationCycle(id, cycleId);
    }

    @PutMapping("/add-employee")
    public ResponseEntity<ResponseDTO<Void>> addEmployeeToProject(@RequestBody AddEmployeeToProjectRequestDTO dto) {
        return projectService.addEmployeeToProject(dto);
    }

    @PutMapping("/remove-employee")
    public ResponseEntity<ResponseDTO<Void>> removeEmployeeFromProject(@RequestBody AddEmployeeToProjectRequestDTO dto) {
        return projectService.removeEmployeeFromProject(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProjectResponseDTO>> patch(
            @PathVariable @Positive Long id,
            @RequestBody ProjectRequestDTO dto) {
        return projectService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable @Positive Long id) {
        return projectService.delete(id);
    }
}
