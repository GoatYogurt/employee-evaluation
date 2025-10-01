package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.CriterionGroupRequestDTO;
import com.vtit.intern.dtos.responses.CriterionGroupResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;


public interface CriterionGroupService {
    PageResponse<CriterionGroupResponseDTO> getAllGroups(String name, String description, org.springframework.data.domain.Pageable pageable);
    CriterionGroupResponseDTO getById(Long id);
    CriterionGroupResponseDTO create(CriterionGroupRequestDTO dto);
    CriterionGroupResponseDTO patch(Long id, CriterionGroupRequestDTO dto);
    void delete(Long id);
}
