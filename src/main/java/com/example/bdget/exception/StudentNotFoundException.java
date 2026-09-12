package com.example.bdget.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) {
        super("No existe un estudiante con id " + id);
    }
}
