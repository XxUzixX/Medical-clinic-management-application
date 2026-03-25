package com.example.clinic.service.impl;

import com.example.clinic.entity.PatientNote;
import com.example.clinic.entity.User;
import com.example.clinic.repository.PatientNoteRepository;
import com.example.clinic.service.PatientNoteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientNoteServiceImpl implements PatientNoteService {
    private final PatientNoteRepository repo;

    public PatientNoteServiceImpl(PatientNoteRepository repo) {
        this.repo = repo;
    }

    @Override
    public PatientNote create(User doctor, User patient, String originalText, String aiText) {
        return create(doctor, patient, originalText, aiText, false);
    }

    @Override
    public List<PatientNote> findForPatient(Long patientId) {
        return repo.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public PatientNote approve(Long noteId, Long doctorId) {
        PatientNote n = repo.findById(noteId).orElseThrow(() -> new RuntimeException("Notatka nie istnieje"));
        if (!n.getDoctor().getId().equals(doctorId))
            throw new RuntimeException("Możesz zatwierdzać tylko swoje notatki.");
        n.setApproved(true);
        return repo.save(n);
    }

    @Override
    public PatientNote updateText(Long noteId, Long doctorId, String originalText, String aiText) {
        PatientNote n = repo.findById(noteId).orElseThrow(() -> new RuntimeException("Notatka nie istnieje"));
        if (!n.getDoctor().getId().equals(doctorId))
            throw new RuntimeException("Możesz edytować tylko swoje notatki.");
        if (originalText != null) n.setOriginalText(originalText);
        if (aiText != null) n.setAiText(aiText);
        // po edycji zdejmij zatwierdzenie, aby ponownie przejrzeć
        n.setApproved(false);
        return repo.save(n);
    }

    @Override
    public void delete(Long noteId, Long doctorId) {
        PatientNote n = repo.findById(noteId).orElseThrow(() -> new RuntimeException("Notatka nie istnieje"));
        if (!n.getDoctor().getId().equals(doctorId))
            throw new RuntimeException("Możesz usuwać tylko swoje notatki.");
        repo.delete(n);
    }


    @Override
    public PatientNote create(User doctor, User patient, String originalText, String aiText, boolean approved) {
        PatientNote n = new PatientNote();
        n.setDoctor(doctor);
        n.setPatient(patient);
        n.setOriginalText(originalText);
        n.setAiText(aiText);
        n.setApproved(approved);
        return repo.save(n);
    }
}