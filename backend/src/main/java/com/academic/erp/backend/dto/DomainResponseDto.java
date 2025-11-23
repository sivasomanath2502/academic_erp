package com.academic.erp.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainResponseDto {

    private Long domainId;
    private String program;
    private Integer capacity;
}
