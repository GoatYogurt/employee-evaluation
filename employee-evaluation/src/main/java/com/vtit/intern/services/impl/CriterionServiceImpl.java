package com.vtit.intern.services.impl;

import com.vtit.intern.models.Criterion;
import com.vtit.intern.repositories.CriterionRepository;
import com.vtit.intern.services.CriterionService;
import org.springframework.stereotype.Service;
import com.vtit.intern.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
public class CriterionServiceImpl implements CriterionService {
    private final CriterionRepository repository;

    public CriterionServiceImpl(CriterionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Criterion> getAllCriteria() {
        return repository.findAll();
    }

    @Override
    public Criterion getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criterion not found with id: " + id));
    }

    @Override
    public Criterion create(Criterion criterion) {
        return repository.save(criterion);
    }

    @Override
    public Criterion update(Long id, Criterion updated) {
        return repository.findById(id).map(criterion -> {
            criterion.setName(updated.getName());
            criterion.setDescription(updated.getDescription());
            criterion.setWeight(updated.getWeight());
            return repository.save(criterion);
        }).orElseThrow(() -> new ResourceNotFoundException("Cannot update. Criterion not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Criterion not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
