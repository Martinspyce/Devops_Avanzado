package com.example.bdget.service;

import com.example.bdget.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<Student> getAllStudents();
    long countStudents();
    Optional<Student> getStudentById(Long id);
    List<Student> getStudentsByName(String name);
    Student createStudent(Student student);
    Student updateStudent(Long id,Student student);
    void deleteStudent(Long id);
}
