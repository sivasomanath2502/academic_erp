package com.academic.erp.backend.service;

import com.academic.erp.backend.dto.DomainResponseDto;
import com.academic.erp.backend.entity.Domain;
import com.academic.erp.backend.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;

    @Override
    public List<DomainResponseDto> getAllDomains() {
        return domainRepository.findAll()
                .stream()
                .map(domain -> DomainResponseDto.builder()
                        .domainId(domain.getDomainId())
                        .program(domain.getProgram())
                        .capacity(domain.getCapacity())
                        .build()
                ).toList();
    }
}
