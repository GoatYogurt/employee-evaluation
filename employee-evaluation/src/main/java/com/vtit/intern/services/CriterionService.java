package com.vtit.intern.services;

import com.vtit.intern.dtos.CriterionDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.models.Criterion;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CriterionService {
    PageResponse<CriterionDTO> getAllCriteria(String name, String description, Double minWeight, Double maxWeight, Pageable pageable);
    CriterionDTO getById(Long id);
    CriterionDTO create(CriterionDTO criterionDto);
    CriterionDTO update(Long id, CriterionDTO criterionDto);
    void delete(Long id);
    CriterionDTO patch(Long id, CriterionDTO criterionDto);
}
