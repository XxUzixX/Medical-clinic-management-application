// src/main/java/com/example/clinic/service/BookingService.java
package com.example.clinic.service;

import com.example.clinic.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public interface BookingService {
    // znajdź lekarzy po specjalizacji
    List<User> findDoctorsBySpecialization(String specialization);

    // wolne sloty (co 30 min) na najbliższe 'days' dni
    Map<User, List<LocalDateTime>> findFreeSlotsForDoctors(List<User> doctors, LocalDate from, int days);

    // wybierz "najlepszą" propozycję (pierwszy najbliższy wolny termin)
    Optional<Proposal> bestProposal(String specialization, LocalDate from, int days);

    record Proposal(User doctor, LocalDateTime slot) {}
}