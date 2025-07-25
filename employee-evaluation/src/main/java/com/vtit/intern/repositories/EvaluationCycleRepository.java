package com.vtit.intern.repositories;

import com.vtit.intern.models.EvaluationCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationCycleRepository extends JpaRepository<EvaluationCycle, Long> {
}
