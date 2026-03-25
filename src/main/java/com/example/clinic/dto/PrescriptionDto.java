package com.example.clinic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PrescriptionDto {
    private Long id;
    private String medication;
    private String dosage;
    private String instructions;
    private String doctorName;
    private LocalDateTime issuedAt;
    private String patientEmail;
}
