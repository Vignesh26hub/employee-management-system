package com.ems.service.impl;

import com.ems.dto.EmployeeRequestDto;
import com.ems.dto.EmployeeResponseDto;
import com.ems.entity.Employee;
import com.ems.exception.DuplicateEmailException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.EmployeeRepository;
import com.ems.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the EmployeeService interface.
 * Implements core business logic, data validation, exception triggering,
 * and entity-to-DTO conversion.
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    /**
     * Constructor Injection (recommended best practice over @Autowired field injection).
     * Makes dependencies explicit and facilitates easy unit testing with Mockito.
     *
     * @param employeeRepository repository instance supplied by Spring IoC container
     */
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto) {
        // Business Rule: Email must be unique
        if (employeeRepository.existsByEmail(employeeRequestDto.getEmail())) {
            throw new DuplicateEmailException(
                    employeeRequestDto.getEmail(),
                    "Please provide a different, unique email address."
            );
        }

        // Convert Request DTO to JPA Entity
        Employee employee = mapToEntity(employeeRequestDto);

        // Save entity to MySQL database
        Employee savedEmployee = employeeRepository.save(employee);

        // Return Response DTO with generated database ID
        return mapToResponseDto(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToResponseDto(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        // Step 1: Verify employee exists
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Step 2: If the email has been changed, verify that no OTHER employee is already using it
        if (employeeRepository.existsByEmailAndIdNot(employeeRequestDto.getEmail(), id)) {
            throw new DuplicateEmailException(
                    employeeRequestDto.getEmail(),
                    "Another employee is already registered with this email."
            );
        }

        // Step 3: Update fields
        existingEmployee.setFirstName(employeeRequestDto.getFirstName());
        existingEmployee.setLastName(employeeRequestDto.getLastName());
        existingEmployee.setEmail(employeeRequestDto.getEmail());
        existingEmployee.setDepartment(employeeRequestDto.getDepartment());
        existingEmployee.setDesignation(employeeRequestDto.getDesignation());
        existingEmployee.setSalary(employeeRequestDto.getSalary());

        // Step 4: Persist updated employee
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return mapToResponseDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        // Verify employee exists before attempting deletion
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> searchEmployeesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name query parameter cannot be null or empty");
        }

        String trimmedName = name.trim();
        List<Employee> employees = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(trimmedName, trimmedName);

        return employees.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> searchEmployeesByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department search query parameter cannot be null or empty");
        }

        List<Employee> employees = employeeRepository.findByDepartmentIgnoreCase(department.trim());

        return employees.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Helper Mapper Methods (DTO <-> Entity)
    // =========================================================================

    /**
     * Maps an EmployeeRequestDto to an Employee JPA Entity.
     */
    private Employee mapToEntity(EmployeeRequestDto dto) {
        return new Employee(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getDepartment(),
                dto.getDesignation(),
                dto.getSalary()
        );
    }

    /**
     * Maps an Employee JPA Entity to an EmployeeResponseDto.
     */
    private EmployeeResponseDto mapToResponseDto(Employee entity) {
        return new EmployeeResponseDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getDepartment(),
                entity.getDesignation(),
                entity.getSalary()
        );
    }
}
