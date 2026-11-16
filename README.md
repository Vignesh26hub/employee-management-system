# 🏢 Employee Management System (EMS) - Backend REST API

A professional, enterprise-grade Employee Management System backend designed and implemented as a portfolio showcase project for an aspiring **Java Backend Developer**. 

Built with **Java 21**, **Spring Boot 3.3.5**, **Spring Data JPA**, **Hibernate**, and **MySQL**, adhering strictly to clean code principles, SOLID design standards, pure Java object modeling (no Lombok, full understanding of constructors/encapsulation), and test-driven best practices with **JUnit 5** and **Mockito**.

---

## 📌 Table of Contents
1. [Key Features](#-key-features)
2. [Technology Stack](#-technology-stack)
3. [Clean Layered Architecture](#-clean-layered-architecture)
4. [Project Directory Structure](#-project-directory-structure)
5. [Database Design & Setup](#-database-design--setup)
6. [Environment Configuration & Security](#-environment-configuration--security)
7. [Getting Started (Run Locally)](#-getting-started-run-locally)
8. [REST API Documentation](#-rest-api-documentation)
9. [Input Validation & Error Handling](#-input-validation--error-handling)
10. [Unit Testing with JUnit 5 & Mockito](#-unit-testing-with-junit-5--mockito)
11. [Postman Testing Guide](#-postman-testing-guide)

---

## ✨ Key Features
- **Full CRUD Lifecycle**: Create, Read (all or by ID), Update, and Delete employee records.
- **Advanced Querying**: Search employees by name (case-insensitive partial matching) and by department.
- **Robust Bean Validation**: Pre-persistence validation on required fields, email formatting, string lengths, and positive salaries.
- **Business Rule Enforcement**: Strict email uniqueness checking on both employee creation and updates.
- **Centralized Global Exception Handling**: Uniform JSON error responses mapped to proper HTTP status codes (`200`, `201`, `204`, `400`, `404`, `409`, `500`).
- **Clean DTO Pattern**: Clear separation between API contracts (`EmployeeRequestDto`, `EmployeeResponseDto`) and database entities (`Employee`).
- **Pure Java OOP**: Explicit constructors (no-args and parameterized), getters, setters, `equals`, `hashCode`, and `toString` without relying on magical code-generation libraries (no Lombok).
- **Automated Unit Testing**: 26 unit tests covering 100% of controller and service edge cases using Mockito mocks.
- **Zero-Secret Commits**: Credentials externalized via environment variables and `.gitignore`.

---

## 🛠 Technology Stack
| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java (JDK) | 21 (LTS) | Core programming language leveraging modern Java features |
| **Framework** | Spring Boot | 3.3.5 | Production-ready REST application framework |
| **ORM / Data** | Spring Data JPA / Hibernate | 6.x | Object-Relational Mapping and repository abstraction |
| **Validation** | Jakarta Bean Validation / Hibernate Validator | 3.0.x | Request payload declarative validation |
| **Database** | MySQL | 8.0+ | Relational persistence storage |
| **Test DB** | H2 Database | 2.x | Fast, zero-setup in-memory database for automated test suites |
| **Build Tool** | Apache Maven | 3.9+ | Dependency and lifecycle build management |
| **Unit Testing**| JUnit 5 & Mockito | 5.10+ / 5.11+ | Unit testing and mock-driven testing |
| **API Client** | Postman | v2.1.0 | Manual API exploration and test automation |

---

## 🏛 Clean Layered Architecture

The application strictly enforces a **Clean Layered Architecture** with unidirectional data flow:

```
[ Client / Postman ]
        │ HTTP Request (JSON)
        ▼
┌────────────────────────────────────────────────────────┐
│ 1. Controller Layer (@RestController)                 │
│    - Validates request payload (@Valid)               │
│    - Dispatches to Service Layer                      │
│    - Returns ResponseEntity with HTTP Status Codes    │
└──────────────────────────┬─────────────────────────────┘
                           │ DTOs (EmployeeRequestDto)
                           ▼
┌────────────────────────────────────────────────────────┐
│ 2. Service Layer (@Service, @Transactional)           │
│    - Enforces business rules (e.g. Email Uniqueness)  │
│    - Converts between Entities and DTOs               │
│    - Throws domain exceptions (ResourceNotFound, etc) │
└──────────────────────────┬─────────────────────────────┘
                           │ Entities (Employee)
                           ▼
┌────────────────────────────────────────────────────────┐
│ 3. Repository Layer (@Repository, JpaRepository)      │
│    - Interacts with Hibernate / JPA                   │
│    - Dynamic query derivation (findByEmail, etc.)     │
└──────────────────────────┬─────────────────────────────┘
                           │ SQL Queries (JDBC)
                           ▼
┌────────────────────────────────────────────────────────┐
│ 4. Database Layer (MySQL)                             │
│    - Table: employees                                  │
└────────────────────────────────────────────────────────┘
```

### Cross-Cutting Layers
- **DTOs (`com.ems.dto`)**: Decouples API inputs and outputs from internal database tables.
- **Exception Handling (`com.ems.exception`)**: Central `@RestControllerAdvice` intercepts exceptions across all layers and formats standardized JSON error payloads.

---

## 📂 Project Directory Structure

```
employee-management-system/
│
├── .gitignore                                    # Git ignore rules (build artifacts, secrets)
├── .env.example                                  # Template for environment variables
├── pom.xml                                       # Maven build script and dependencies
├── README.md                                     # Project documentation
│
├── postman/
│   └── Employee_Management_System.postman_collection.json # Ready-to-import Postman collection
│
├── src/
│   ├── main/
│   │   ├── java/com/ems/
│   │   │   ├── EmployeeManagementSystemApplication.java  # Spring Boot entry point
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   └── EmployeeController.java       # REST endpoints (/api/v1/employees)
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── EmployeeRequestDto.java       # Input DTO with validation annotations
│   │   │   │   └── EmployeeResponseDto.java      # Output DTO with database ID
│   │   │   │
│   │   │   ├── entity/
│   │   │   │   └── Employee.java                 # JPA Entity mapped to "employees" table
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── DuplicateEmailException.java  # 409 Conflict exception
│   │   │   │   ├── ErrorResponse.java            # Standardized JSON error response structure
│   │   │   │   ├── GlobalExceptionHandler.java   # @RestControllerAdvice handler
│   │   │   │   └── ResourceNotFoundException.java# 404 Not Found exception
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   └── EmployeeRepository.java       # Spring Data JPA interface
│   │   │   │
│   │   │   └── service/
│   │   │       ├── EmployeeService.java          # Service interface
│   │   │       └── impl/
│   │   │           └── EmployeeServiceImpl.java  # Service business logic implementation
│   │   │
│   │   └── resources/
│   │       └── application.properties            # Main application & MySQL configuration
│   │
│   └── test/
│       ├── java/com/ems/
│       │   ├── controller/
│       │   │   └── EmployeeControllerTest.java   # Web layer MockMvc unit tests
│       │   └── service/
│       │       └── EmployeeServiceTest.java      # Service layer Mockito unit tests
│       │
│       └── resources/
│           └── application-test.properties       # H2 in-memory test configuration
```

---

## 🗄 Database Design & Setup

### Database Details
- **DBMS**: MySQL 8.0+
- **Database Name**: `employee_db`
- **Table Name**: `employees`

### Table Schema (`employees`)
| Column Name | SQL Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | Unique employee identifier |
| `first_name` | `VARCHAR(50)` | `NOT NULL` | Employee's first name |
| `last_name` | `VARCHAR(50)` | `NOT NULL` | Employee's last name |
| `email` | `VARCHAR(100)` | `NOT NULL, UNIQUE` | Unique employee email address |
| `department` | `VARCHAR(50)` | `NOT NULL` | Assigned department |
| `designation`| `VARCHAR(50)` | `NOT NULL` | Job role or title |
| `salary` | `DOUBLE` | `NOT NULL` | Employee compensation |

> [!NOTE]
> Hibernate's `ddl-auto=update` is configured in `application.properties`. When you start the application, Hibernate will automatically create the database schema and table structure if they do not already exist.

---

## 🔐 Environment Configuration & Security

**Security Rule**: Never commit passwords or database credentials to GitHub.

`application.properties` uses Spring expression placeholders with default fallback values:
```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/employee_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:password}
```

### Setting Environment Variables (Optional):
Before starting the application, you can set your environment variables:

**On Windows (PowerShell):**
```powershell
$env:DB_USERNAME="your_mysql_username"
$env:DB_PASSWORD="your_actual_password"
```

**On Linux / macOS (Bash):**
```bash
export DB_USERNAME="your_mysql_username"
export DB_PASSWORD="your_actual_password"
```

---

## 🚀 Getting Started (Run Locally)

### Prerequisites
1. **JDK 21** installed and configured (`java -version`).
2. **Apache Maven 3.9+** installed (`mvn -version`).
3. **MySQL Server** installed and running on port `3306`.

### Step 1: Create Database in MySQL
Log in to your MySQL terminal and run:
```sql
CREATE DATABASE IF NOT EXISTS employee_db;
```

### Step 2: Build the Project
Open a terminal in the project root directory and run:
```bash
mvn clean install
```

### Step 3: Run the Spring Boot Application
```bash
mvn spring-boot:run
```
The server will start at: `http://localhost:8080`.

---

## 📡 REST API Documentation

### API Summary Table
| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/employees` | Create a new employee | `201 CREATED` |
| **GET** | `/api/v1/employees` | Retrieve all employees | `200 OK` |
| **GET** | `/api/v1/employees/{id}` | Retrieve employee by ID | `200 OK` / `404 NOT FOUND` |
| **PUT** | `/api/v1/employees/{id}` | Update existing employee | `200 OK` / `400` / `404` / `409` |
| **DELETE** | `/api/v1/employees/{id}` | Delete employee by ID | `204 NO CONTENT` / `404 NOT FOUND` |
| **GET** | `/api/v1/employees/search/name?query={name}` | Search employees by name | `200 OK` |
| **GET** | `/api/v1/employees/search/department?query={dept}` | Search employees by department | `200 OK` |

---

### Detailed Endpoint Specifications

#### 1. Create Employee
- **URL**: `POST /api/v1/employees`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "designation": "Software Engineer",
  "salary": 85000.0
}
```
- **Response** (`201 CREATED`):
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "designation": "Software Engineer",
  "salary": 85000.0
}
```

---

#### 2. Get All Employees
- **URL**: `GET /api/v1/employees`
- **Response** (`200 OK`):
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "department": "Engineering",
    "designation": "Software Engineer",
    "salary": 85000.0
  }
]
```

---

#### 3. Get Employee By ID
- **URL**: `GET /api/v1/employees/1`
- **Response** (`200 OK`):
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "designation": "Software Engineer",
  "salary": 85000.0
}
```
- **Response** (`404 NOT FOUND` if ID does not exist):
```json
{
  "timestamp": "2026-11-16 11:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id : '99'",
  "path": "/api/v1/employees/99"
}
```

---

#### 4. Update Employee
- **URL**: `PUT /api/v1/employees/1`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "designation": "Senior Software Engineer",
  "salary": 98000.0
}
```
- **Response** (`200 OK`):
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "designation": "Senior Software Engineer",
  "salary": 98000.0
}
```

---

#### 5. Delete Employee
- **URL**: `DELETE /api/v1/employees/1`
- **Response** (`204 NO CONTENT` with empty body)

---

#### 6. Search Employees By Name
- **URL**: `GET /api/v1/employees/search/name?query=John`
- **Response** (`200 OK`):
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "department": "Engineering",
    "designation": "Senior Software Engineer",
    "salary": 98000.0
  }
]
```

---

#### 7. Search Employees By Department
- **URL**: `GET /api/v1/employees/search/department?query=Engineering`
- **Response** (`200 OK`):
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "department": "Engineering",
    "designation": "Senior Software Engineer",
    "salary": 98000.0
  }
]
```

---

## 🛡 Input Validation & Error Handling

All incoming request payloads are validated against declarative Jakarta Bean Validation rules defined in `EmployeeRequestDto`.

### Validation Rules
| Field | Annotation | Validation Condition | Error Message |
| :--- | :--- | :--- | :--- |
| `firstName` | `@NotBlank`, `@Size` | Non-blank, 2 to 50 characters | *"First name is required and cannot be blank"* |
| `lastName` | `@NotBlank`, `@Size` | Non-blank, 2 to 50 characters | *"Last name is required and cannot be blank"* |
| `email` | `@NotBlank`, `@Email` | Valid email syntax, max 100 chars | *"Email must be a valid email address format"* |
| `department`| `@NotBlank`, `@Size` | Non-blank, 2 to 50 characters | *"Department is required and cannot be blank"* |
| `designation`| `@NotBlank`, `@Size`| Non-blank, 2 to 50 characters | *"Designation is required and cannot be blank"* |
| `salary` | `@NotNull`, `@Positive`| Required, numeric value > 0.0 | *"Salary must be greater than zero"* |

### Sample Validation Error Response (`400 BAD REQUEST`)
```json
{
  "timestamp": "2026-11-16 11:35:10",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/v1/employees",
  "validationErrors": {
    "firstName": "First name is required and cannot be blank",
    "email": "Email must be a valid email address format (e.g., john.doe@example.com)",
    "salary": "Salary must be greater than zero"
  }
}
```

### Sample Duplicate Email Error Response (`409 CONFLICT`)
```json
{
  "timestamp": "2026-11-16 11:35:22",
  "status": 409,
  "error": "Conflict",
  "message": "Employee with email 'john.doe@example.com' already exists. Please provide a different, unique email address.",
  "path": "/api/v1/employees"
}
```

---

## 🧪 Unit Testing with JUnit 5 & Mockito

The project contains **26 comprehensive unit tests** testing both business logic and REST endpoint behavior in full isolation:

### Test Breakdown
1. **`EmployeeServiceTest.java` (15 Tests)**:
   - Tests business logic using `@ExtendWith(MockitoExtension.class)`.
   - Uses `@Mock` for `EmployeeRepository` and `@InjectMocks` for `EmployeeServiceImpl`.
   - Verifies positive scenarios, duplicate email checks, non-existing resource exceptions, and search queries.

2. **`EmployeeControllerTest.java` (11 Tests)**:
   - Tests web layer routing and HTTP contracts using `@WebMvcTest(EmployeeController.class)` and `MockMvc`.
   - Verifies status codes (`201`, `200`, `204`, `400`, `404`, `409`), JSON deserialization, and Bean Validation integration.

### Running the Test Suite
Execute the following Maven command from the project root:
```bash
mvn test
```

### Test Results
```
[INFO] Running com.ems.controller.EmployeeControllerTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.ems.service.EmployeeServiceTest
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 📮 Postman Testing Guide

A complete Postman collection is included in the project under `postman/Employee_Management_System.postman_collection.json`.

### How to Import and Test
1. Open **Postman**.
2. Click **Import** in the top left corner.
3. Select the file:
   ```
   employee-management-system/postman/Employee_Management_System.postman_collection.json
   ```
4. Verify the collection variable `baseUrl` is set to `http://localhost:8080/api/v1/employees`.

### Recommended Test Execution Sequence
1. **1. Create Employee (201 Created)**: Send a valid employee payload. Notice the returned `id`.
2. **2. Create Employee - Invalid Data (400 Bad Request)**: Send an invalid payload. Notice the detailed `validationErrors` map in the response.
3. **3. Create Employee - Duplicate Email (409 Conflict)**: Attempt to create an employee with the same email. Notice the `409 Conflict` status.
4. **4. Get All Employees (200 OK)**: Retrieve the list of all created employees.
5. **5. Get Employee By ID (200 OK)**: Retrieve employee with ID `1`.
6. **6. Get Employee By ID - Not Found (404 Not Found)**: Try retrieving ID `9999`.
7. **7. Update Employee (200 OK)**: Update designation and salary.
8. **8. Search Employee By Name (200 OK)**: Search using `?query=John`.
9. **9. Search Employee By Department (200 OK)**: Search using `?query=Engineering`.
10. **10. Delete Employee (204 No Content)**: Delete employee with ID `1`.
11. **11. Delete Employee - Not Found (404 Not Found)**: Verify deleting a non-existent ID returns `404`.

---

## 👨‍💻 Author & Portfolio Note
Developed by **Vignesh** as a showcase project demonstrating:
- Proficiency in Java 21, Spring Boot, Spring Data JPA, and Hibernate.
- Clean layered architecture and SOLID design patterns.
- Thorough automated testing with JUnit 5 and Mockito.
- Professional API documentation and Postman test automation.
