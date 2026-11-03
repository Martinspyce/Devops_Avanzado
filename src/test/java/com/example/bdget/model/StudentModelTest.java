package com.example.bdget.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StudentModelTest {
    @Test
    void testGettersAndSetters() {
        Student student = new Student();
        student.setId(1L);
        student.setName("John");
        student.setEmail("john@example.com");
        assertEquals(1L, student.getId());
        assertEquals("John", student.getName());
        assertEquals("john@example.com", student.getEmail());
    }
}
