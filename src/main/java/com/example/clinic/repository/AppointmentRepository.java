package com.example.clinic.repository;

import com.example.clinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Znajdź wizyty dla konkretnego lekarza w określonym przedziale czasu
    List<Appointment> findByDoctorIdAndDateTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    // Znajdź wszystkie wizyty lekarza
    List<Appointment> findByDoctorId(Long doctorId);

    // Znajdź wszystkie wizyty pacjenta
    List<Appointment> findByPatientId(Long patientId);
}