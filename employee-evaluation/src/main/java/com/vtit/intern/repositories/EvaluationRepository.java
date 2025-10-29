package com.vtit.intern.repositories;

import com.vtit.intern.models.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Page<Evaluation> findByEmployeeId(Long employeeId, Pageable pageable);

    @Query("SELECT e FROM Evaluation e WHERE " +
            "e.isDeleted = false AND " +
            "(:employeeId IS NULL OR e.employee.id = :employeeId)")
    Page<Evaluation> searchEvaluations(
            @Param("employeeId") Long employeeId,
            Pageable pageable);

    Optional<Evaluation> findByIdAndIsDeletedFalse(Long id);

    Optional<Evaluation> findByEmployee_IdAndProject_IdAndEvaluationCycle_IdAndIsDeletedFalse(Long employeeId, Long projectId, Long evaluationCycleId);

    Set<Evaluation> findByProject_IdAndEvaluationCycle_IdAndIsDeletedFalse(Long projectId, Long evaluationCycleId);

    @Query(
            value = """
                     SELECT\s
                         COUNT(DISTINCT pe.employee_id) AS total_assigned,
                         COUNT(DISTINCT ev.employee_id) AS total_evaluated
                     FROM project_employee pe
                     JOIN project p\s
                         ON p.id = pe.project_id\s
                         AND p.is_deleted = false
                     JOIN evaluation_cycle_project ecp\s
                         ON ecp.project_id = p.id
                     JOIN evaluation_cycle ec
                         ON ec.id = ecp.evaluation_cycle_id
                         AND ec.is_deleted = false
                     JOIN employee e\s
                         ON e.id = pe.employee_id
                         AND e.is_deleted = false
                     LEFT JOIN evaluation ev\s
                         ON ev.employee_id = pe.employee_id\s
                        AND ev.evaluation_cycle_id = ecp.evaluation_cycle_id
                        AND ev.is_deleted = false
                     WHERE ecp.evaluation_cycle_id = :evaluationCycleId
                    \s""",
            nativeQuery = true
    )
    Object getEvaluationProgressRaw(@Param("evaluationCycleId") Long evaluationCycleId);

    @Query("""
                SELECT\s
                    CASE\s
                        WHEN e.totalScore < 2 THEN '0–1.99'
                        WHEN e.totalScore < 4 THEN '2–3.99'
                        ELSE '4–5'
                    END AS range,
                    COUNT(e) AS count
                FROM Evaluation e
                WHERE e.evaluationCycle.id = :evaluationCycleId
                  AND (e.isDeleted = false OR e.isDeleted IS NULL)
                GROUP BY\s
                    CASE\s
                        WHEN e.totalScore < 2 THEN '0–1.99'
                        WHEN e.totalScore < 4 THEN '2–3.99'
                        ELSE '4–5'
                    END
                ORDER BY range
            """)
    List<Object[]> getScoreDistributionRaw(@Param("evaluationCycleId") Long evaluationCycleId);

    @Query("""
                SELECT e
                FROM Evaluation e
                WHERE e.evaluationCycle.id = :evaluationCycleId
                  AND (e.isDeleted = false OR e.isDeleted IS NULL)
                ORDER BY e.totalScore DESC
            """)
    List<Evaluation> findTopEvaluations(@Param("evaluationCycleId") Long evaluationCycleId, Pageable pageable);
}
