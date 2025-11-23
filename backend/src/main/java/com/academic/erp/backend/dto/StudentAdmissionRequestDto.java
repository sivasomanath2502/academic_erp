package com.academic.erp.backend.dto;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAdmissionRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String photographPath;

    @NotNull(message = "Domain ID is required")
    private Long domainId;

    @NotNull(message = "Join year is required")
    @Min(value = 2000, message = "Join year must be valid")
    @Max(value = 2100, message = "Join year must be valid")
    private Integer joinYear;
}
