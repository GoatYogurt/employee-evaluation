package com.vtit.intern.repositories;

import com.vtit.intern.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("""
    SELECT e FROM Employee e
    WHERE e.isDeleted = false
    AND (:fullName IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')))
    AND (:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%')))
    AND (:role IS NULL OR LOWER(e.role) LIKE LOWER(CONCAT('%', :role, '%')))
    AND (:username IS NULL OR LOWER(e.username) LIKE LOWER(CONCAT('%', :username, '%')))
    AND (:email IS NULL OR LOWER(e.email) LIKE LOWER(CONCAT('%', :email, '%')))
    AND (:level IS NULL OR LOWER(e.level) LIKE LOWER(CONCAT('%', :level, '%')))
    AND (:staffCode IS NULL OR e.staffCode = :staffCode)
""")
    Page<Employee> searchEmployees(
            @Param("fullName") String fullName,
            @Param("username") String username,
            @Param("email") String email,
            @Param("department") String department,
            @Param("role") String role,
            @Param("level") String level,
            @Param("staffCode") Integer staffCode,
            Pageable pageable);

    boolean existsByUsernameAndIsDeletedFalse(String username);
    boolean existsByEmailAndIsDeletedFalse(String email);
    boolean existsByStaffCodeAndIsDeletedFalse(Integer staffCode);
    Optional<Employee> findByUsernameAndIsDeletedFalse(String username);
    Page<Employee> findAllByIsDeletedFalse(Pageable pageable);
    Set<Employee> findByIdInAndIsDeletedFalse(Set<Long> ids);
    Optional<Employee> findByIdAndIsDeletedFalse(Long id);
}
