package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.CriterionDTO;
import com.vtit.intern.models.Criterion;
import com.vtit.intern.repositories.CriterionRepository;
import com.vtit.intern.services.CriterionService;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vtit.intern.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
public class CriterionServiceImpl implements CriterionService {
    @Autowired
    private final CriterionRepository repository;
    @Autowired
    private final ModelMapper modelMapper;

    public CriterionServiceImpl(CriterionRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CriterionDTO> getAllCriteria() {
        return repository.findAll().stream()
                .map(criterion -> modelMapper.map(criterion, CriterionDTO.class))
                .toList();
    }

    @Override
    public CriterionDTO getById(Long id) {
        return repository.findById(id)
                .map(criterion -> modelMapper.map(criterion, CriterionDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Criterion not found with id: " + id));
    }

    @Override
    public CriterionDTO create(CriterionDTO criterionDto) {
        Criterion criterion = modelMapper.map(criterionDto, Criterion.class);
        Criterion savedCriterion = repository.save(criterion);
        return modelMapper.map(savedCriterion, CriterionDTO.class);
    }

    @Override
    public CriterionDTO update(Long id, CriterionDTO criterionDto) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot update. Criterion not found with id: " + id);
        }

        Criterion criterion = modelMapper.map(criterionDto, Criterion.class);
        criterion.setId(id); // Ensure the ID is set for the update
        Criterion updatedCriterion = repository.save(criterion);
        return modelMapper.map(updatedCriterion, CriterionDTO.class);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Criterion not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
