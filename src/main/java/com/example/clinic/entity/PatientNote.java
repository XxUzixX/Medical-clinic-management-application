package com.example.clinic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patient_notes")
public class PatientNote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="doctor_id")
    private User doctor;

    @ManyToOne(optional=false) @JoinColumn(name="patient_id")
    private User patient;

    @Column(columnDefinition = "TEXT")
    private String originalText;   // wpis lekarza

    @Column(columnDefinition = "TEXT")
    private String aiText;         // wynik AI

    @Column(nullable = false)
    private boolean approved = false; // zatwierdzona przez lekarza

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @Column(nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }


}