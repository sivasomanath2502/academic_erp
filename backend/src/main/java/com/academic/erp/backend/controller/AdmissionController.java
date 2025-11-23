package com.academic.erp.backend.controller;

import com.academic.erp.backend.dto.StudentAdmissionRequestDto;
import com.academic.erp.backend.dto.StudentResponseDto;
import com.academic.erp.backend.service.AdmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin
public class AdmissionController {

    private final AdmissionService admissionService;

    @PostMapping("/admit")
    public StudentResponseDto admitStudent(@Valid @RequestBody StudentAdmissionRequestDto request) {
        return admissionService.admitStudent(request);
    }
}
