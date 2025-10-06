package com.vtit.intern.repositories;

import com.vtit.intern.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByCode(String code);

    @Query("SELECT p FROM Project p " +
            "WHERE (:code IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%'))) " +
            "AND (:managerName IS NULL OR LOWER(p.manager.fullName) LIKE LOWER(CONCAT('%', :managerName, '%'))) " +
            "AND (:isOdc IS NULL OR p.isOdc = :isOdc) " +
            "AND p.isDeleted = false")

    Page<Project> searchProjects(
            @Param("code") String code,
            @Param("managerName") String managerName,
            @Param("isOdc") Boolean isOdc,
            Pageable pageable
    );

    Page<Project> findByIdIn(Iterable<Long> ids, Pageable pageable);
}
