package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.ProjectRequestDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponeDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.ProjectSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ProjectService {
    ResponseEntity<ResponseDTO<ProjectResponeDTO>> create(ProjectRequestDTO dto);
    ResponseEntity<ResponseDTO<ProjectResponeDTO>> getById(Long id);
    ResponseEntity<ResponseDTO<PageResponse<ProjectResponeDTO>>> getAll(ProjectSearchDTO searchDTO, Pageable pageable);
    ResponseEntity<ResponseDTO<ProjectResponeDTO>> patch(Long id, ProjectRequestDTO dto);
    void delete(Long id);
}

