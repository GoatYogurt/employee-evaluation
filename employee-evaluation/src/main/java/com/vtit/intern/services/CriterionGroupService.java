package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.CriterionGroupRequestDTO;
import com.vtit.intern.dtos.responses.CriterionGroupResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.http.ResponseEntity;


public interface CriterionGroupService {
    ResponseEntity<ResponseDTO<PageResponse<CriterionGroupResponseDTO>>> getAllGroups(String name, String description, org.springframework.data.domain.Pageable pageable);
    ResponseEntity<ResponseDTO<CriterionGroupResponseDTO>> getById(Long id);
    ResponseEntity<ResponseDTO<CriterionGroupResponseDTO>> create(CriterionGroupRequestDTO dto);
    ResponseEntity<ResponseDTO<CriterionGroupResponseDTO>> patch(Long id, CriterionGroupRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> delete(Long id);
}
