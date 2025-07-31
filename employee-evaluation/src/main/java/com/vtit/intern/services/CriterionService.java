package com.vtit.intern.services;

import com.vtit.intern.dtos.CriterionDTO;
import com.vtit.intern.responses.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CriterionService {
    PageResponse<CriterionDTO> getAllCriteria(String name, String description, Double minWeight, Double maxWeight, Pageable pageable);
    CriterionDTO getById(Long id);
    CriterionDTO create(CriterionDTO criterionDto);
    CriterionDTO update(Long id, CriterionDTO criterionDto);
    void delete(Long id);
    CriterionDTO patch(Long id, CriterionDTO criterionDto);
}
