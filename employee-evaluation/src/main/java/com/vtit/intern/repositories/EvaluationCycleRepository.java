package com.vtit.intern.repositories;

import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.enums.EvaluationCycleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;


@Repository
public interface EvaluationCycleRepository extends JpaRepository<EvaluationCycle, Long> {
    @Query("SELECT e FROM EvaluationCycle e WHERE " +
            "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:startDate IS NULL OR e.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.endDate <= :endDate)")
    Page<EvaluationCycle> searchEvaluationCycles(
            @Param("name") String name,
            @Param("description") String description,
            @Param("status") EvaluationCycleStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    Set<EvaluationCycle> findByIdIn(Set<Long> ids);
}
