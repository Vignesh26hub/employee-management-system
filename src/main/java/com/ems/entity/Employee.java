package com.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Entity class representing an Employee record in the MySQL database.
 * Mapped to the "employees" table using JPA (Jakarta Persistence API) and Hibernate.
 *
 * NOTE: Lombok is intentionally NOT used so you can understand standard Java
 * OOP concepts including default constructors, parameterized constructors,
 * getters, setters, equals, hashCode, and toString.
 */
@Entity
@Table(name = "employees")
public class Employee {

    /**
     * Primary Key for the Employee entity.
     * Uses MySQL AUTO_INCREMENT strategy via GenerationType.IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Employee's first name.
     * Cannot be null in the database.
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * Employee's last name.
     * Cannot be null in the database.
     */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Employee's official email address.
     * Must be unique across all employee records in the database.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Department where the employee works (e.g., "Engineering", "HR", "Finance").
     */
    @Column(name = "department", nullable = false, length = 50)
    private String department;

    /**
     * Job designation/title (e.g., "Software Engineer", "Senior Analyst").
     */
    @Column(name = "designation", nullable = false, length = 50)
    private String designation;

    /**
     * Employee monthly/annual compensation.
     */
    @Column(name = "salary", nullable = false)
    private Double salary;

    /**
     * Default No-Argument Constructor.
     * Required by JPA/Hibernate to instantiate the object via reflection
     * when loading data from the database.
     */
    public Employee() {
    }

    /**
     * Parameterized Constructor without ID.
     * Useful when creating new Employee records before database ID generation.
     *
     * @param firstName   Employee first name
     * @param lastName    Employee last name
     * @param email       Employee email address
     * @param department  Employee department
     * @param designation Employee designation
     * @param salary      Employee salary
     */
    public Employee(String firstName, String lastName, String email, 
                    String department, String designation, Double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
    }

    /**
     * Full Parameterized Constructor (including ID).
     * Useful when updating, mapping from DTOs, or testing.
     *
     * @param id          Employee unique identifier
     * @param firstName   Employee first name
     * @param lastName    Employee last name
     * @param email       Employee email address
     * @param department  Employee department
     * @param designation Employee designation
     * @param salary      Employee salary
     */
    public Employee(Long id, String firstName, String lastName, String email, 
                    String department, String designation, Double salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    // =========================================================================
    // equals, hashCode, and toString
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) &&
               Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", salary=" + salary +
                '}';
    }
}
