package com.vtit.intern.repositories;

import com.vtit.intern.models.Criterion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CriterionRepository extends JpaRepository<Criterion, Long> {
    @Query("SELECT c FROM Criterion c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:minWeight IS NULL OR c.weight >= :minWeight) AND " +
            "(:maxWeight IS NULL OR c.weight <= :maxWeight)")
    Page<Criterion> searchCriteria(
            @Param("name") String name,
            @Param("description") String description,
            @Param("minWeight") Double minWeight,
            @Param("maxWeight") Double maxWeight,
            Pageable pageable);
}

