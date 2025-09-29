package com.vtit.intern.repositories;

import com.vtit.intern.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE " +
            "(:fullName IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
            "(:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
            "(:role IS NULL OR LOWER(e.role) LIKE LOWER(CONCAT('%', :role, '%'))) AND " +
    "(:username IS NULL OR LOWER(e.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:email IS NULL OR LOWER(e.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<Employee> searchEmployees(
            @Param("fullName") String fullName,
            @Param("username") String username,
            @Param("email") String email,
            @Param("department") String department,
            @Param("role") String role,
            Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existByStaffCode(Integer staffCode);
    Optional<Employee> findByUsername(String username);
    List<Employee> findAll();
}
