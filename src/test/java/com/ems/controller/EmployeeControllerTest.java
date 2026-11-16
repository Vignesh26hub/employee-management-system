package com.ems.controller;

import com.ems.dto.EmployeeRequestDto;
import com.ems.dto.EmployeeResponseDto;
import com.ems.exception.DuplicateEmailException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web Layer Unit Tests for {@link EmployeeController} using {@link WebMvcTest} and {@link MockMvc}.
 * Tests HTTP method bindings, JSON serialization/deserialization, Bean Validation, and HTTP Status Codes.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // 1. CREATE EMPLOYEE (POST) TESTS
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/employees - Success returns 201 Created")
    void testCreateEmployee_Success() throws Exception {
        // Given
        EmployeeRequestDto requestDto = new EmployeeRequestDto(
                "Alice", "Wonder", "alice.wonder@example.com", "Engineering", "Backend Developer", 80000.0
        );

        EmployeeResponseDto responseDto = new EmployeeResponseDto(
                1L, "Alice", "Wonder", "alice.wonder@example.com", "Engineering", "Backend Developer", 80000.0
        );

        given(employeeService.createEmployee(any(EmployeeRequestDto.class))).willReturn(responseDto);

        // When / Then
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.lastName", is("Wonder")))
                .andExpect(jsonPath("$.email", is("alice.wonder@example.com")))
                .andExpect(jsonPath("$.salary", is(80000.0)));
    }

    @Test
    @DisplayName("POST /api/v1/employees - Invalid Payload returns 400 Bad Request with field errors")
    void testCreateEmployee_ValidationFailure_Returns400BadRequest() throws Exception {
        // Given invalid payload: blank first name, invalid email, negative salary
        EmployeeRequestDto invalidRequest = new EmployeeRequestDto(
                "", "", "invalid-email-format", "", "", -500.0
        );

        // When / Then
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.validationErrors.firstName").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.salary").exists());
    }

    @Test
    @DisplayName("POST /api/v1/employees - Duplicate Email returns 409 Conflict")
    void testCreateEmployee_DuplicateEmail_Returns409Conflict() throws Exception {
        // Given
        EmployeeRequestDto requestDto = new EmployeeRequestDto(
                "Alice", "Wonder", "alice.wonder@example.com", "Engineering", "Backend Developer", 80000.0
        );

        given(employeeService.createEmployee(any(EmployeeRequestDto.class)))
                .willThrow(new DuplicateEmailException("alice.wonder@example.com", "Email already exists"));

        // When / Then
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("alice.wonder@example.com")));
    }

    // =========================================================================
    // 2. GET ALL EMPLOYEES TESTS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/employees - Success returns 200 OK and list")
    void testGetAllEmployees_Success() throws Exception {
        // Given
        EmployeeResponseDto emp1 = new EmployeeResponseDto(
                1L, "Alice", "Wonder", "alice.wonder@example.com", "Engineering", "Dev", 80000.0
        );
        EmployeeResponseDto emp2 = new EmployeeResponseDto(
                2L, "Bob", "Builder", "bob.builder@example.com", "DevOps", "DevOps Eng", 85000.0
        );

        given(employeeService.getAllEmployees()).willReturn(List.of(emp1, emp2));

        // When / Then
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Alice")))
                .andExpect(jsonPath("$[1].firstName", is("Bob")));
    }

    // =========================================================================
    // 3. GET EMPLOYEE BY ID TESTS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Existing ID returns 200 OK")
    void testGetEmployeeById_Success() throws Exception {
        // Given
        EmployeeResponseDto responseDto = new EmployeeResponseDto(
                1L, "Alice", "Wonder", "alice.wonder@example.com", "Engineering", "Dev", 80000.0
        );
        given(employeeService.getEmployeeById(1L)).willReturn(responseDto);

        // When / Then
        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Alice")));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Non-existing ID returns 404 Not Found")
    void testGetEmployeeById_NotFound_Returns404NotFound() throws Exception {
        // Given
        given(employeeService.getEmployeeById(99L))
                .willThrow(new ResourceNotFoundException("Employee", "id", 99L));

        // When / Then
        mockMvc.perform(get("/api/v1/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Employee not found with id : '99'")));
    }

    // =========================================================================
    // 4. UPDATE EMPLOYEE (PUT) TESTS
    // =========================================================================

    @Test
    @DisplayName("PUT /api/v1/employees/{id} - Success returns 200 OK")
    void testUpdateEmployee_Success() throws Exception {
        // Given
        EmployeeRequestDto updateRequest = new EmployeeRequestDto(
                "Alice", "Updated", "alice.updated@example.com", "Engineering", "Lead Dev", 95000.0
        );

        EmployeeResponseDto updatedResponse = new EmployeeResponseDto(
                1L, "Alice", "Updated", "alice.updated@example.com", "Engineering", "Lead Dev", 95000.0
        );

        given(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDto.class))).willReturn(updatedResponse);

        // When / Then
        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Updated")))
                .andExpect(jsonPath("$.salary", is(95000.0)));
    }

    // =========================================================================
    // 5. DELETE EMPLOYEE (DELETE) TESTS
    // =========================================================================

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Success returns 204 No Content")
    void testDeleteEmployee_Success() throws Exception {
        // Given
        willDoNothing().given(employeeService).deleteEmployee(1L);

        // When / Then
        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Not Found returns 404 Not Found")
    void testDeleteEmployee_NotFound_Returns404NotFound() throws Exception {
        // Given
        willThrow(new ResourceNotFoundException("Employee", "id", 99L))
                .given(employeeService).deleteEmployee(99L);

        // When / Then
        mockMvc.perform(delete("/api/v1/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    // =========================================================================
    // 6. SEARCH EMPLOYEES BY NAME TESTS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/employees/search/name?query={name} - Success returns 200 OK")
    void testSearchEmployeesByName_Success() throws Exception {
        // Given
        EmployeeResponseDto emp1 = new EmployeeResponseDto(
                1L, "Alice", "Wonder", "alice.wonder@example.com", "Engineering", "Dev", 80000.0
        );
        given(employeeService.searchEmployeesByName("Alice")).willReturn(List.of(emp1));

        // When / Then
        mockMvc.perform(get("/api/v1/employees/search/name")
                        .param("query", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Alice")));
    }

    // =========================================================================
    // 7. SEARCH EMPLOYEES BY DEPARTMENT TESTS
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/employees/search/department?query={department} - Success returns 200 OK")
    void testSearchEmployeesByDepartment_Success() throws Exception {
        // Given
        EmployeeResponseDto emp1 = new EmployeeResponseDto(
                1L, "Alice", "Wonder", "alice.wonder@example.com", "Engineering", "Dev", 80000.0
        );
        given(employeeService.searchEmployeesByDepartment("Engineering")).willReturn(List.of(emp1));

        // When / Then
        mockMvc.perform(get("/api/v1/employees/search/department")
                        .param("query", "Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].department", is("Engineering")));
    }
}
