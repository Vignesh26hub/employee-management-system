package com.ems.service;

import com.ems.dto.EmployeeRequestDto;
import com.ems.dto.EmployeeResponseDto;

import java.util.List;

/**
 * Service interface defining the business contract for Employee management operations.
 * Decouples controller layer from concrete implementation logic.
 */
public interface EmployeeService {

    /**
     * Create and persist a new Employee.
     *
     * @param employeeRequestDto incoming payload with employee details
     * @return persisted employee details wrapped in a response DTO
     * @throws com.ems.exception.DuplicateEmailException if email is already taken
     */
    EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto);

    /**
     * Retrieve all employees currently in the system.
     *
     * @return list of all employees as response DTOs
     */
    List<EmployeeResponseDto> getAllEmployees();

    /**
     * Retrieve a specific employee by their database ID.
     *
     * @param id employee unique identifier
     * @return employee response DTO
     * @throws com.ems.exception.ResourceNotFoundException if no employee exists with this ID
     */
    EmployeeResponseDto getEmployeeById(Long id);

    /**
     * Update an existing employee's details.
     *
     * @param id employee unique identifier
     * @param employeeRequestDto updated details
     * @return updated employee as response DTO
     * @throws com.ems.exception.ResourceNotFoundException if no employee exists with this ID
     * @throws com.ems.exception.DuplicateEmailException if email belongs to another employee
     */
    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto);

    /**
     * Delete an employee record by their database ID.
     *
     * @param id employee unique identifier
     * @throws com.ems.exception.ResourceNotFoundException if no employee exists with this ID
     */
    void deleteEmployee(Long id);

    /**
     * Search for employees whose first or last name contains the given search term.
     *
     * @param name search term
     * @return list of matching employees
     */
    List<EmployeeResponseDto> searchEmployeesByName(String name);

    /**
     * Search for employees belonging to a specific department.
     *
     * @param department department name
     * @return list of employees in the given department
     */
    List<EmployeeResponseDto> searchEmployeesByDepartment(String department);
}
