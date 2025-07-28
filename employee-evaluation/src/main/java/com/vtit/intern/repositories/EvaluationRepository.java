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
            "(:employeeId IS NULL OR e.employee.id = :employeeId) AND " +
            "(:criterionId IS NULL OR e.criterion.id = :criterionId) AND " +
            "(:minScore IS NULL OR e.score >= :minScore) AND " +
            "(:maxScore IS NULL OR e.score <= :maxScore) AND " +
            "(:comment IS NULL OR LOWER(e.comment) LIKE LOWER(CONCAT('%', :comment, '%'))) AND " +
            "(:startDate IS NULL OR e.evaluationDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.evaluationDate <= :endDate)")
    Page<Evaluation> searchEvaluations(
            @Param("employeeId") Long employeeId,
            @Param("criterionId") Long criterionId,
            @Param("minScore") Double minScore,
            @Param("maxScore") Double maxScore,
            @Param("comment") String comment,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

}
