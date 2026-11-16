package com.ems.controller;

import com.ems.dto.EmployeeRequestDto;
import com.ems.dto.EmployeeResponseDto;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing endpoints for Employee Management System.
 * Follows RESTful best practices:
 * - Resource-based URI naming
 * - Proper HTTP Verbs (GET, POST, PUT, DELETE)
 * - Standard HTTP Status Codes (200, 201, 204, 400, 404, 409, 500)
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Constructor Injection of EmployeeService dependency.
     */
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 1. CREATE EMPLOYEE
     * HTTP POST /api/v1/employees
     *
     * @param requestDto validated request body
     * @return created Employee details with HTTP 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @Valid @RequestBody EmployeeRequestDto requestDto) {
        EmployeeResponseDto createdEmployee = employeeService.createEmployee(requestDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    /**
     * 2. GET ALL EMPLOYEES
     * HTTP GET /api/v1/employees
     *
     * @return list of all employees with HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * 3. GET EMPLOYEE BY ID
     * HTTP GET /api/v1/employees/{id}
     *
     * @param id employee primary key
     * @return employee details with HTTP 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") Long id) {
        EmployeeResponseDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * 4. UPDATE EMPLOYEE
     * HTTP PUT /api/v1/employees/{id}
     *
     * @param id         employee primary key
     * @param requestDto updated employee information
     * @return updated employee details with HTTP 200 OK status
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable("id") Long id,
            @Valid @RequestBody EmployeeRequestDto requestDto) {
        EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(id, requestDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * 5. DELETE EMPLOYEE
     * HTTP DELETE /api/v1/employees/{id}
     *
     * @param id employee primary key
     * @return empty response with HTTP 204 NO CONTENT status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 6. SEARCH EMPLOYEES BY NAME
     * HTTP GET /api/v1/employees/search/name?query={name}
     *
     * @param query search keyword for first name or last name
     * @return matching employees with HTTP 200 OK status
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<EmployeeResponseDto>> searchEmployeesByName(
            @RequestParam("query") String query) {
        List<EmployeeResponseDto> employees = employeeService.searchEmployeesByName(query);
        return ResponseEntity.ok(employees);
    }

    /**
     * 7. SEARCH EMPLOYEES BY DEPARTMENT
     * HTTP GET /api/v1/employees/search/department?query={department}
     *
     * @param query department name keyword
     * @return matching employees with HTTP 200 OK status
     */
    @GetMapping("/search/department")
    public ResponseEntity<List<EmployeeResponseDto>> searchEmployeesByDepartment(
            @RequestParam("query") String query) {
        List<EmployeeResponseDto> employees = employeeService.searchEmployeesByDepartment(query);
        return ResponseEntity.ok(employees);
    }
}
