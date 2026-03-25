package com.example.clinic.service;

import com.example.clinic.entity.PatientNote;
import com.example.clinic.entity.User;

import java.util.List;

public interface PatientNoteService {
    PatientNote create(User doctor, User patient, String originalText, String aiText);
    List<PatientNote> findForPatient(Long patientId);
    PatientNote approve(Long noteId, Long doctorId);           // ustawia approved=true
    PatientNote updateText(Long noteId, Long doctorId, String newOriginalText, String newAiText);
    void delete(Long noteId, Long doctorId);
    PatientNote create(User doctor, User patient, String originalText, String aiText, boolean approved);
}