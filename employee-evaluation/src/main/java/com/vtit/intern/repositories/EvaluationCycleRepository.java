package com.vtit.intern.repositories;

import com.vtit.intern.models.EvaluationCycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EvaluationCycleRepository extends JpaRepository<EvaluationCycle, Long> {
    Page<EvaluationCycle> findByStatus(String status, Pageable pageable);
}
