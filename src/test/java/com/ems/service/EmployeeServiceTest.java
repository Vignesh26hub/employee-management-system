package com.ems.service;

import com.ems.dto.EmployeeRequestDto;
import com.ems.dto.EmployeeResponseDto;
import com.ems.entity.Employee;
import com.ems.exception.DuplicateEmailException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.EmployeeRepository;
import com.ems.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EmployeeServiceImpl} using JUnit 5 and Mockito.
 * Tests business logic isolation without requiring a live database connection.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDto requestDto;

    @BeforeEach
    void setUp() {
        employee = new Employee(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "Engineering",
                "Software Engineer",
                85000.0
        );

        requestDto = new EmployeeRequestDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "Engineering",
                "Software Engineer",
                85000.0
        );
    }

    // =========================================================================
    // 1. CREATE EMPLOYEE TESTS
    // =========================================================================

    @Test
    @DisplayName("Given valid EmployeeRequestDto, when createEmployee, then return saved EmployeeResponseDto")
    void testCreateEmployee_Success() {
        // Given
        given(employeeRepository.existsByEmail("john.doe@example.com")).willReturn(false);
        given(employeeRepository.save(any(Employee.class))).willReturn(employee);

        // When
        EmployeeResponseDto responseDto = employeeService.createEmployee(requestDto);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(responseDto.getFirstName()).isEqualTo("John");
        assertThat(responseDto.getSalary()).isEqualTo(85000.0);

        verify(employeeRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given duplicate email, when createEmployee, then throw DuplicateEmailException")
    void testCreateEmployee_DuplicateEmail_ThrowsException() {
        // Given
        given(employeeRepository.existsByEmail("john.doe@example.com")).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> employeeService.createEmployee(requestDto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("john.doe@example.com");

        verify(employeeRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // =========================================================================
    // 2. GET ALL EMPLOYEES TESTS
    // =========================================================================

    @Test
    @DisplayName("Given employees exist, when getAllEmployees, then return list of EmployeeResponseDto")
    void testGetAllEmployees_Success() {
        // Given
        Employee employee2 = new Employee(
                2L, "Jane", "Smith", "jane.smith@example.com", "Marketing", "Marketing Lead", 75000.0
        );
        given(employeeRepository.findAll()).willReturn(List.of(employee, employee2));

        // When
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees();

        // Then
        assertThat(employees).hasSize(2);
        assertThat(employees.get(0).getEmail()).isEqualTo("john.doe@example.com");
        assertThat(employees.get(1).getEmail()).isEqualTo("jane.smith@example.com");
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given no employees exist, when getAllEmployees, then return empty list")
    void testGetAllEmployees_EmptyList() {
        // Given
        given(employeeRepository.findAll()).willReturn(Collections.emptyList());

        // When
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees();

        // Then
        assertThat(employees).isEmpty();
        verify(employeeRepository, times(1)).findAll();
    }

    // =========================================================================
    // 3. GET EMPLOYEE BY ID TESTS
    // =========================================================================

    @Test
    @DisplayName("Given existing employee ID, when getEmployeeById, then return EmployeeResponseDto")
    void testGetEmployeeById_Success() {
        // Given
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

        // When
        EmployeeResponseDto responseDto = employeeService.getEmployeeById(1L);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getFirstName()).isEqualTo("John");
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Given non-existing employee ID, when getEmployeeById, then throw ResourceNotFoundException")
    void testGetEmployeeById_NotFound_ThrowsException() {
        // Given
        given(employeeRepository.findById(99L)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id : '99'");

        verify(employeeRepository, times(1)).findById(99L);
    }

    // =========================================================================
    // 4. UPDATE EMPLOYEE TESTS
    // =========================================================================

    @Test
    @DisplayName("Given valid updated details, when updateEmployee, then return updated EmployeeResponseDto")
    void testUpdateEmployee_Success() {
        // Given
        EmployeeRequestDto updateDto = new EmployeeRequestDto(
                "Johnny",
                "Doe",
                "johnny.updated@example.com",
                "Engineering",
                "Senior Software Engineer",
                95000.0
        );

        Employee updatedEmployee = new Employee(
                1L,
                "Johnny",
                "Doe",
                "johnny.updated@example.com",
                "Engineering",
                "Senior Software Engineer",
                95000.0
        );

        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));
        given(employeeRepository.existsByEmailAndIdNot("johnny.updated@example.com", 1L)).willReturn(false);
        given(employeeRepository.save(any(Employee.class))).willReturn(updatedEmployee);

        // When
        EmployeeResponseDto responseDto = employeeService.updateEmployee(1L, updateDto);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getFirstName()).isEqualTo("Johnny");
        assertThat(responseDto.getEmail()).isEqualTo("johnny.updated@example.com");
        assertThat(responseDto.getDesignation()).isEqualTo("Senior Software Engineer");
        assertThat(responseDto.getSalary()).isEqualTo(95000.0);

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).existsByEmailAndIdNot("johnny.updated@example.com", 1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given non-existing ID, when updateEmployee, then throw ResourceNotFoundException")
    void testUpdateEmployee_NotFound_ThrowsException() {
        // Given
        given(employeeRepository.findById(99L)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> employeeService.updateEmployee(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id : '99'");

        verify(employeeRepository, times(1)).findById(99L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given email belongs to another employee, when updateEmployee, then throw DuplicateEmailException")
    void testUpdateEmployee_DuplicateEmail_ThrowsException() {
        // Given
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));
        given(employeeRepository.existsByEmailAndIdNot("john.doe@example.com", 1L)).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> employeeService.updateEmployee(1L, requestDto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Another employee is already registered with this email");

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).existsByEmailAndIdNot("john.doe@example.com", 1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // =========================================================================
    // 5. DELETE EMPLOYEE TESTS
    // =========================================================================

    @Test
    @DisplayName("Given existing employee ID, when deleteEmployee, then delete successfully")
    void testDeleteEmployee_Success() {
        // Given
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

        // When
        employeeService.deleteEmployee(1L);

        // Then
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("Given non-existing employee ID, when deleteEmployee, then throw ResourceNotFoundException")
    void testDeleteEmployee_NotFound_ThrowsException() {
        // Given
        given(employeeRepository.findById(99L)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id : '99'");

        verify(employeeRepository, times(1)).findById(99L);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    // =========================================================================
    // 6. SEARCH EMPLOYEES BY NAME TESTS
    // =========================================================================

    @Test
    @DisplayName("Given search name, when searchEmployeesByName, then return matching employees")
    void testSearchEmployeesByName_Success() {
        // Given
        given(employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("john", "john"))
                .willReturn(List.of(employee));

        // When
        List<EmployeeResponseDto> results = employeeService.searchEmployeesByName("john");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("John");
        verify(employeeRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("john", "john");
    }

    @Test
    @DisplayName("Given empty search name, when searchEmployeesByName, then throw IllegalArgumentException")
    void testSearchEmployeesByName_Blank_ThrowsException() {
        // When / Then
        assertThatThrownBy(() -> employeeService.searchEmployeesByName("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Search name query parameter cannot be null or empty");

        verify(employeeRepository, never())
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), anyString());
    }

    // =========================================================================
    // 7. SEARCH EMPLOYEES BY DEPARTMENT TESTS
    // =========================================================================

    @Test
    @DisplayName("Given department name, when searchEmployeesByDepartment, then return matching employees")
    void testSearchEmployeesByDepartment_Success() {
        // Given
        given(employeeRepository.findByDepartmentIgnoreCase("Engineering"))
                .willReturn(List.of(employee));

        // When
        List<EmployeeResponseDto> results = employeeService.searchEmployeesByDepartment("Engineering");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDepartment()).isEqualTo("Engineering");
        verify(employeeRepository, times(1)).findByDepartmentIgnoreCase("Engineering");
    }

    @Test
    @DisplayName("Given empty department name, when searchEmployeesByDepartment, then throw IllegalArgumentException")
    void testSearchEmployeesByDepartment_Blank_ThrowsException() {
        // When / Then
        assertThatThrownBy(() -> employeeService.searchEmployeesByDepartment(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Department search query parameter cannot be null or empty");

        verify(employeeRepository, never()).findByDepartmentIgnoreCase(anyString());
    }
}
