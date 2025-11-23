package com.academic.erp.backend.controller;

import com.academic.erp.backend.dto.StudentResponseDto;
import com.academic.erp.backend.entity.Student;
import com.academic.erp.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin
public class StudentQueryController {

    private final StudentRepository studentRepository;

    @GetMapping
    public List<StudentResponseDto> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(s -> StudentResponseDto.builder()
                        .studentId(s.getStudentId())
                        .rollNumber(s.getRollNumber())
                        .firstName(s.getFirstName())
                        .lastName(s.getLastName())
                        .email(s.getEmail())
                        .domainProgram(s.getDomain().getProgram())
                        .joinYear(s.getJoinYear())
                        .build()
                ).toList();
    }

    @GetMapping("/{id}")
    public StudentResponseDto getStudentById(@PathVariable Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return StudentResponseDto.builder()
                .studentId(s.getStudentId())
                .rollNumber(s.getRollNumber())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .domainProgram(s.getDomain().getProgram())
                .joinYear(s.getJoinYear())
                .build();
    }

    @GetMapping("/roll/{rollNumber}")
    public StudentResponseDto getStudentByRoll(@PathVariable String rollNumber) {
        Student s = studentRepository.findAll()
                .stream()
                .filter(st -> rollNumber.equalsIgnoreCase(st.getRollNumber()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return StudentResponseDto.builder()
                .studentId(s.getStudentId())
                .rollNumber(s.getRollNumber())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .domainProgram(s.getDomain().getProgram())
                .joinYear(s.getJoinYear())
                .build();
    }
}
