package com.vtit.intern.services;

import com.vtit.intern.models.Criterion;

import java.util.List;

public interface CriterionService {
    List<Criterion> getAllCriteria();
    Criterion getById(Long id);
    Criterion create(Criterion criterion);
    Criterion update(Long id, Criterion updated);
    void delete(Long id);
}
