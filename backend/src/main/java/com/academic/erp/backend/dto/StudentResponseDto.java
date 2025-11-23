package com.academic.erp.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDto {

    private Long studentId;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String domainProgram;
    private Integer joinYear;
}
