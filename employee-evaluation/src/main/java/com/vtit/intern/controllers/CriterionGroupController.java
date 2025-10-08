package com.vtit.intern.controllers;
import com.vtit.intern.dtos.requests.CriterionGroupRequestDTO;
import com.vtit.intern.dtos.responses.CriterionGroupResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.searches.CriterionGroupSearchDTO;
import com.vtit.intern.services.CriterionGroupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/criterion-groups")
@Validated
public class CriterionGroupController {

    @Autowired
    private CriterionGroupService groupService;

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    public PageResponse<CriterionGroupResponseDTO> getAllGroups(
            @RequestBody(required = false) CriterionGroupSearchDTO searchDTO,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        String name = (searchDTO != null) ? searchDTO.getName() : null;
        String desc = (searchDTO != null) ? searchDTO.getDescription() : null;

        return groupService.getAllGroups(name, desc, PageRequest.of(page, size, sort));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CriterionGroupResponseDTO> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(groupService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CriterionGroupResponseDTO> create(@Valid @RequestBody CriterionGroupRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PatchMapping("{id}")
    public ResponseEntity<CriterionGroupResponseDTO> patch(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @RequestBody CriterionGroupRequestDTO dto
    ) {
        return ResponseEntity.ok(groupService.patch(id, dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID must be positive") Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
