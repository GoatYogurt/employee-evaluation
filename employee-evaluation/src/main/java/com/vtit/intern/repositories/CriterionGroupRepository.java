package com.vtit.intern.repositories;

import com.vtit.intern.models.CriterionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CriterionGroupRepository extends JpaRepository<CriterionGroup, Long> {

    @Query("SELECT g FROM CriterionGroup g " +
            "WHERE g.isDeleted = false " +
            "WHERE (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:description IS NULL OR LOWER(g.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
            "AND g.isDeleted = false")
    Page<CriterionGroup> searchGroups(
            @Param("name") String name,
            @Param("description") String description,
            Pageable pageable
    );

    Optional<CriterionGroup> findByIdAndIsDeletedFalse(Long id);
}
