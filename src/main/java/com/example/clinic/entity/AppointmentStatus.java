package com.example.clinic.entity;

public enum AppointmentStatus {
    REQUESTED,            // Wizyta zarejestrowana przez pacjenta (oczekuje na decyzję lekarza)
    APPROVED_BY_DOCTOR,   // Lekarz zaakceptował
    PROPOSED_NEW_TIME,    // Lekarz zaproponował nowy termin
    CONFIRMED_BY_PATIENT, // Pacjent potwierdził zaproponowany termin
    REJECTED              // Wizyta odrzucona
}