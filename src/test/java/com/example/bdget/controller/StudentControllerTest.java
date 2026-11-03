package com.example.bdget.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import com.example.bdget.model.Student;
import com.example.bdget.service.StudentService;
import com.example.bdget.exception.StudentNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService service;

    @Autowired
    private ObjectMapper mapper;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setName("John");
        student.setEmail("john@example.com");
    }

    @Test
    void testGetAllStudents() throws Exception {
        when(service.getAllStudents()).thenReturn(Arrays.asList(student));
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(student))));
    }

    @Test
    void testGetStudentById() throws Exception {
        when(service.getStudentById(1L)).thenReturn(Optional.of(student));
        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(student)));
    }

    @Test
    void testGetStudentsByName() throws Exception {
        when(service.getStudentsByName("John")).thenReturn(Arrays.asList(student));
        mockMvc.perform(get("/students/search").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(student))));
    }

    @Test
    void testCreateStudent() throws Exception {
        when(service.createStudent(any(Student.class))).thenReturn(student);
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(student)));
    }

    @Test
    void testCreateStudentInvalidEmail() throws Exception {
        Student invalidStudent = new Student();
        invalidStudent.setName("John");
        invalidStudent.setEmail("invalid-email-format");

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStudent() throws Exception {
        when(service.updateStudent(eq(1L), any(Student.class))).thenReturn(student);
        mockMvc.perform(put("/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(student)));
    }

    @Test
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isOk());
        verify(service).deleteStudent(1L);
    }

    @Test
    void testDeleteStudentNotFound() throws Exception {
        doThrow(new StudentNotFoundException(99L)).when(service).deleteStudent(99L);

        mockMvc.perform(delete("/students/99"))
                .andExpect(status().isNotFound());
    }
}
