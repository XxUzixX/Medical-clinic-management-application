package com.example.clinic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String medication;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private String instructions;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Column(nullable = false)
    private LocalDateTime issuedAt;
}
