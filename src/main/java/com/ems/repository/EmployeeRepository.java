package com.ems.repository;

import com.ems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Employee entity.
 * Provides out-of-the-box CRUD operations and custom derived query methods.
 * Spring dynamically creates proxy implementations at runtime.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find an Employee by exact email address.
     *
     * @param email the employee email to search
     * @return an Optional containing the Employee if found, or empty Optional
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Check if an Employee with the specified email already exists.
     *
     * @param email the email to verify
     * @return true if an employee exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if another Employee (different from the given ID) uses the specified email.
     * Essential for validating email uniqueness during update operations.
     *
     * @param email the email to check
     * @param id the employee ID being updated
     * @return true if another employee already has this email
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Search employees whose first name OR last name contains the search term (case-insensitive).
     *
     * @param firstName keyword for first name
     * @param lastName  keyword for last name
     * @return list of matching employees
     */
    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    /**
     * Search employees by department name (case-insensitive).
     *
     * @param department department name
     * @return list of employees belonging to the department
     */
    List<Employee> findByDepartmentIgnoreCase(String department);
}
