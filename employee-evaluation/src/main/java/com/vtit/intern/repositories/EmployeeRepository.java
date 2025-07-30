package com.vtit.intern.repositories;

import com.vtit.intern.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE " +
            "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
            "(:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%'))) AND " +
            "(:role IS NULL OR LOWER(e.role) LIKE LOWER(CONCAT('%', :role, '%'))) AND " +
            "(:salaryMin IS NULL OR e.salary >= :salaryMin) AND " +
            "(:salaryMax IS NULL OR e.salary <= :salaryMax)"
    + " AND (:username IS NULL OR LOWER(e.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:email IS NULL OR LOWER(e.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<Employee> searchEmployees(
            @Param("name") String name,
            @Param("username") String username,
            @Param("email") String email,
            @Param("department") String department,
            @Param("position") String position,
            @Param("role") String role,
            @Param("salaryMin") Double salaryMin,
            @Param("salaryMax") Double salaryMax,
            Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Employee> findByUsername(String username);
}
