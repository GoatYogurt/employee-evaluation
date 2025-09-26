package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.CriterionRequestDTO;
import com.vtit.intern.dtos.responses.CriterionResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface CriterionService {
    PageResponse<CriterionResponseDTO> getAllCriteria(String name, String description, Double minWeight, Double maxWeight, Pageable pageable);
    CriterionResponseDTO getById(Long id);
    CriterionResponseDTO create(CriterionRequestDTO dto);
//    CriterionResponseDTO update(Long id, CriterionRequestDTO dto);
    void delete(Long id);
    CriterionResponseDTO patch(Long id, CriterionRequestDTO dto);
}
