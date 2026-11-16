package com.ems.exception;

/**
 * Custom runtime exception thrown when attempting to create or update an Employee
 * with an email address that is already registered in the system.
 * Maps to HTTP 409 CONFLICT status.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String email, String customDetail) {
        super(String.format("Employee with email '%s' already exists. %s", email, customDetail));
    }
}
