package com.example.bdget.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bdget.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>{
    List<Student> findByNameContainingIgnoreCase(String name);
    List<Student> findByEmailIgnoreCase(String email);
}