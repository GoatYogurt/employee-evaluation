package com.vtit.intern.repositories;

import com.vtit.intern.models.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Page<Evaluation> findByEmployeeId(Long employeeId, Pageable pageable);
}
