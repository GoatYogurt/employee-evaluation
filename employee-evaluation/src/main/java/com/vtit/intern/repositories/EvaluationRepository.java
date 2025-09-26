package com.vtit.intern.repositories;

import com.vtit.intern.models.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Page<Evaluation> findByEmployeeId(Long employeeId, Pageable pageable);

    @Query("SELECT e FROM Evaluation e WHERE " +
            "(:employeeId IS NULL OR e.employee.id = :employeeId)")
    Page<Evaluation> searchEvaluations(
            @Param("employeeId") Long employeeId,
            Pageable pageable);
}
