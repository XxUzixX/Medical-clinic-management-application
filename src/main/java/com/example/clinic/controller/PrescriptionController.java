package com.example.clinic.controller;

import com.example.clinic.dto.PrescriptionDto;
import com.example.clinic.entity.User;
import com.example.clinic.service.PrescriptionService;
import com.example.clinic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PrescriptionController {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionController.class);

    private final PrescriptionService prescriptionService;
    private final UserService userService;

    public PrescriptionController(PrescriptionService prescriptionService, UserService userService) {
        this.prescriptionService = prescriptionService;
        this.userService = userService;
    }

    @GetMapping("/doctor/prescriptions/new")
    public String showPrescriptionForm(Model model) {
        model.addAttribute("prescription", new PrescriptionDto());
        model.addAttribute("patients", userService.findAllPatients());
        return "new_prescription";
    }

    @PostMapping("/doctor/prescriptions")
    public String createPrescription(@ModelAttribute("prescription") PrescriptionDto prescriptionDto, Principal principal) {
        logger.debug("Creating prescription: {} for patientEmail: {}", prescriptionDto, prescriptionDto.getPatientEmail());

        if (prescriptionDto.getPatientEmail() == null) {
            logger.error("patientEmail is null");
            return "redirect:/doctor/prescriptions/new?error";
        }

        User doctor = userService.findByEmail(principal.getName());
        User patient = userService.findByEmail(prescriptionDto.getPatientEmail());
        if (doctor != null && patient != null) {
            logger.debug("Doctor: {}, Patient: {}", doctor.getName(), patient.getName());
            prescriptionService.savePrescription(prescriptionDto, doctor, patient);
            return "redirect:/doctor/prescriptions/new?success";
        }
        logger.error("Error creating prescription: Doctor or patient not found");
        return "redirect:/doctor/prescriptions/new?error";
    }

    @GetMapping("/userhome/prescriptions")
    public String listPrescriptions(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<PrescriptionDto> prescriptions = prescriptionService.findPrescriptionsByPatientId(user.getId());
        model.addAttribute("prescriptions", prescriptions);
        return "prescriptions";
    }
}
