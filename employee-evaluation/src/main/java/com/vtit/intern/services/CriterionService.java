package com.vtit.intern.services;

import com.vtit.intern.dtos.CriterionDTO;
import com.vtit.intern.models.Criterion;

import java.util.List;

public interface CriterionService {
    List<CriterionDTO> getAllCriteria();
    CriterionDTO getById(Long id);
    CriterionDTO create(CriterionDTO criterionDto);
    CriterionDTO update(Long id, CriterionDTO criterionDto);
    void delete(Long id);
}
