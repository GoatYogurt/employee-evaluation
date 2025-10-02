package com.vtit.intern.controllers;

import com.vtit.intern.dtos.requests.ProjectRequestDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponeDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.ProjectSearchDTO;
import com.vtit.intern.services.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
    public ResponseEntity<ResponseDTO<PageResponse<ProjectResponeDTO>>> getAll(
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
    public ResponseEntity<ResponseDTO<ProjectResponeDTO>> getById(
            @PathVariable @Positive Long id) {
        return projectService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ProjectResponeDTO>> create(@Valid @RequestBody ProjectRequestDTO dto) {
        return projectService.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProjectResponeDTO>> patch(
            @PathVariable @Positive Long id,
            @RequestBody ProjectRequestDTO dto) {
        return projectService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
