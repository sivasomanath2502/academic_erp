package com.academic.erp.backend.service;

import com.academic.erp.backend.dto.DomainResponseDto;
import java.util.List;

public interface DomainService {
    List<DomainResponseDto> getAllDomains();
}
