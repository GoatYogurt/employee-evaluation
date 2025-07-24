package com.vtit.intern.services.impl;

import com.vtit.intern.models.Criteria;
import com.vtit.intern.repositories.CriteriaRepository;
import com.vtit.intern.services.CriteriaService;
import org.springframework.stereotype.Service;
import com.vtit.intern.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
public class CriteriaServiceImpl implements CriteriaService {
    private final CriteriaRepository repository;

    public CriteriaServiceImpl(CriteriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Criteria> getAllCriteria() {
        return repository.findAll();
    }

    @Override
    public Criteria getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criteria not found with id: " + id));
    }

    @Override
    public Criteria create(Criteria criteria) {
        return repository.save(criteria);
    }

    @Override
    public Criteria update(Long id, Criteria updated) {
        return repository.findById(id).map(criteria -> {
            criteria.setName(updated.getName());
            criteria.setDescription(updated.getDescription());
            criteria.setWeight(updated.getWeight());
            return repository.save(criteria);
        }).orElseThrow(() -> new ResourceNotFoundException("Cannot update. Criteria not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Criteria not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
