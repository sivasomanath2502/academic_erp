package com.academic.erp.backend.controller;

import com.academic.erp.backend.dto.DomainResponseDto;
import com.academic.erp.backend.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
@CrossOrigin
public class DomainController {

    private final DomainService domainService;

    @GetMapping
    public List<DomainResponseDto> getAllDomains() {
        return domainService.getAllDomains();
    }
}
