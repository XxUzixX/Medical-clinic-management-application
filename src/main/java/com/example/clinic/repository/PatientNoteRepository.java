package com.example.clinic.repository;

import com.example.clinic.entity.PatientNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientNoteRepository extends JpaRepository<PatientNote, Long> {
    List<PatientNote> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}