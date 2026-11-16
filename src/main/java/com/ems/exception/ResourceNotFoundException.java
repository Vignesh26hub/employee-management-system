package com.ems.exception;

/**
 * Custom runtime exception thrown when a requested resource (such as an Employee ID)
 * does not exist in the database.
 * Maps to HTTP 404 NOT FOUND status.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
