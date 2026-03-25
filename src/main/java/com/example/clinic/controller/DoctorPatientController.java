package com.example.clinic.controller;

import com.example.clinic.ai.GroqService;
import com.example.clinic.dto.PrescriptionDto;
import com.example.clinic.entity.User;
import com.example.clinic.entity.PatientNote;
import com.example.clinic.service.PrescriptionService;
import com.example.clinic.service.UserService;
import com.example.clinic.service.PatientNoteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/doctor/patients")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorPatientController {

    private final UserService userService;
    private final PrescriptionService prescriptionService;
    private final GroqService groqService;
    private final PatientNoteService noteService;

    private static final String AI_NOTE_SYSTEM_PROMPT = """
            Jesteś asystentem klinicznym.
        Zwracaj wynik jako CZYSTY TEKST (bez Markdown, bez #, *, -, tabel i kodu).
        Struktura i dokładne nagłówki (po polsku), każdy w osobnej linii i zakończony dwukropkiem:
        Podsumowanie:
        Istotne fakty:
        Pytania na kolejną wizytę:
        - Pod każdym nagłówkiem wypisz punkty, każdy w nowej linii, zaczynając od znaku • (kropka wypunktowania).
        - Zero formatowania Markdown i żadnych nawiasów klamrowych/JSON.
        - Nie stawiaj diagnoz; nie udzielaj porad; tylko porządkuj treść.
        """;

    public DoctorPatientController(UserService userService,
                                   PrescriptionService prescriptionService,
                                   GroqService groqService,
                                   PatientNoteService noteService) {
        this.userService = userService;
        this.prescriptionService = prescriptionService;
        this.groqService = groqService;
        this.noteService = noteService;
    }

    private void populatePatientModel(User patient, Model model) {
        List<PrescriptionDto> prescriptions = prescriptionService.findPrescriptionsByPatientId(patient.getId());
        List<PatientNote> notes = noteService.findForPatient(patient.getId());
        model.addAttribute("patient", patient);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("notes", notes);
    }

    @GetMapping("/{id}")
    public String patientCard(@PathVariable Long id,
                              @RequestParam(value = "tab", required = false, defaultValue = "summary") String tab,
                              Model model) {
        User patient = userService.findById(id);
        if (patient == null) { model.addAttribute("error", "Pacjent nie istnieje"); return "patients"; }

        populatePatientModel(patient, model);
        model.addAttribute("activeTab", tab);
        return "patient_card";
    }

    // Utwórz i ZAPISZ notatkę
    @PostMapping("/{id}/ai")
    public String aiNote(@PathVariable Long id,
                         @RequestParam("note") String note,
                         Principal principal,
                         Model model) {
        User doctor = userService.findByEmail(principal.getName());
        User patient = userService.findById(id);
        if (patient == null) { model.addAttribute("error","Pacjent nie istnieje"); return "patients"; }

        String ai = groqService.chatText(AI_NOTE_SYSTEM_PROMPT, "Uporządkuj notatkę:\n\n" + note);

        noteService.create(doctor, patient, note, ai);
        return "redirect:/doctor/patients/" + id + "?saved";
    }

    // Zatwierdzanie notatki
    @PostMapping("/{patientId}/notes/{noteId}/approve")
    public String approveNote(@PathVariable Long patientId,
                              @PathVariable Long noteId,
                              Principal principal) {
        User doctor = userService.findByEmail(principal.getName());
        noteService.approve(noteId, doctor.getId());
        return "redirect:/doctor/patients/" + patientId + "?approved";
    }

    // Usuwanie notatki
    @PostMapping("/{patientId}/notes/{noteId}/delete")
    public String deleteNote(@PathVariable Long patientId,
                             @PathVariable Long noteId,
                             Principal principal) {
        User doctor = userService.findByEmail(principal.getName());
        noteService.delete(noteId, doctor.getId());
        return "redirect:/doctor/patients/" + patientId + "?deletedNote";
    }
    @PostMapping("/{id}/notes/draft")
    public String draftNote(@PathVariable Long id,
                            @RequestParam("originalText") String originalText,
                            Principal principal,
                            Model model) {
        User patient = userService.findById(id);
        if (patient == null) { model.addAttribute("error","Pacjent nie istnieje"); return "patients"; }

        String ai = groqService.chatText(AI_NOTE_SYSTEM_PROMPT, "Uporządkuj notatkę:\n\n" + originalText);
        populatePatientModel(patient, model);

        model.addAttribute("draftOriginal", originalText);
        model.addAttribute("draftAi", ai);
        model.addAttribute("activeTab", "newNote");
        return "patient_card";
    }
    // Edycja notatki
    @PostMapping("/{patientId}/notes/{noteId}/edit")
    public String editNote(@PathVariable Long patientId,
                           @PathVariable Long noteId,
                           @RequestParam("originalText") String originalText,
                           @RequestParam("aiText") String aiText,
                           Principal principal) {
        User doctor = userService.findByEmail(principal.getName());
        noteService.updateText(noteId, doctor.getId(), originalText, aiText);
        return "redirect:/doctor/patients/" + patientId + "?edited";
    }

    // Usuwanie recepty (lekarz może usunąć)
    @PostMapping("/{patientId}/prescriptions/{prescriptionId}/delete")
    public String deletePrescription(@PathVariable Long patientId,
                                     @PathVariable Long prescriptionId) {
        prescriptionService.deleteById(prescriptionId);
        return "redirect:/doctor/patients/" + patientId + "?deletedRx";
    }
    @PostMapping("/{id}/notes/create")
    public String createNote(@PathVariable Long id,
                             @RequestParam("originalText") String originalText,
                             @RequestParam(value = "aiText", required = false) String aiText,
                             @RequestParam(value = "approve", required = false, defaultValue = "false") boolean approve,
                             Principal principal,
                             RedirectAttributes ra) {
        User doctor = userService.findByEmail(principal.getName());
        User patient = userService.findById(id);
        if (patient == null) { return "redirect:/doctor/patients?error"; }

        noteService.create(doctor, patient, originalText, aiText, approve);
        ra.addAttribute("created", ""); // będzie widoczne jako parametr bez wartości
        if (approve) {
            ra.addAttribute("approved", "");
        }
        ra.addAttribute("tab", "notes");
        return "redirect:/doctor/patients/{id}";
    }

}