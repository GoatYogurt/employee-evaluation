package com.vtit.intern.services;

import com.vtit.intern.dtos.CriteriaDTO;
import com.vtit.intern.models.Criteria;

import java.util.List;

public interface CriteriaService {
    List<Criteria> getAllCriteria();
    Criteria getById(Long id);
    Criteria create(Criteria criteria);
    Criteria update(Long id, Criteria updated);
    void delete(Long id);
}
