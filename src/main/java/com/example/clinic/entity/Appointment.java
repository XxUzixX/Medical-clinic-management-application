package com.example.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Pacjent
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Lekarz
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    // Termin wizyty
    @Column(nullable = false)
    private LocalDateTime dateTime;

    // Status wizyty
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
}