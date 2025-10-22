package com.vtit.intern.repositories;

import com.vtit.intern.models.Evaluation;
import com.vtit.intern.models.EvaluationScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

public interface EvaluationScoreRepository extends JpaRepository<EvaluationScore, Long> {

    @Query("SELECT es FROM EvaluationScore es WHERE " +
            "es.isDeleted = false AND " +
            "(:criterionId IS NULL OR es.criterion.id = :criterionId) AND " +
            "(:evaluationId IS NULL OR es.evaluation.id = :evaluationId) AND " +
            "(:minScore IS NULL OR es.score >= :minScore) AND " +
            "(:maxScore IS NULL OR es.score <= :maxScore)")
    Page<EvaluationScore> searchEvaluationScore(
            @Param("criterionId") Long criterionId,
            @Param("evaluationId") Long evaluationId,
            @Param("minScore") Double minScore,
            @Param("maxScore") Double maxScore,
            Pageable pageable
    );

    Optional<EvaluationScore> findByIdAndIsDeletedFalse(Long id);
    Optional<EvaluationScore> findByEvaluationIdAndCriterionIdAndIsDeletedFalse(Long evaluationId, Long criterionId);

}
