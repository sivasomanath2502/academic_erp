package com.academic.erp.backend.controller;

import com.academic.erp.backend.dto.StudentResponseDto;
import com.academic.erp.backend.service.StudentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin
public class StudentQueryController {

    private final StudentQueryService queryService;

    @GetMapping
    public List<StudentResponseDto> getAllStudents() {
        return queryService.getAllStudents();
    }
}
