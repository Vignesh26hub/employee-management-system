package com.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Entry Point for Employee Management System.
 * 
 * The @SpringBootApplication annotation enables:
 * - @Configuration: Marks this class as a source of bean definitions.
 * - @EnableAutoConfiguration: Tells Spring Boot to configure beans based on classpath settings.
 * - @ComponentScan: Scans for Spring components (@RestController, @Service, @Repository, etc.)
 *   in the current package ("com.ems") and all sub-packages.
 */
@SpringBootApplication
public class EmployeeManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementSystemApplication.class, args);
    }
}
