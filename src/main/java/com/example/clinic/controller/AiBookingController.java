package com.example.clinic.controller;

import com.example.clinic.ai.AiTriageService;
import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.entity.User;
import com.example.clinic.service.AppointmentService;
import com.example.clinic.service.BookingService;
import com.example.clinic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/booking/ai")
public class AiBookingController {

    private final AiTriageService triage;
    private final BookingService booking;
    private final UserService userService;
    private final AppointmentService appointmentService;

    public AiBookingController(AiTriageService triage, BookingService booking,
                               UserService userService, AppointmentService appointmentService) {
        this.triage = triage;
        this.booking = booking;
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String showForm() {
        return "booking_ai";
    }

    @PostMapping("/analyze")
    public String analyzeNeed(@RequestParam("description") String description, Model model) {
        var result = triage.inferSpecialization(description);
        String spec = result.specialization();

        var doctors = booking.findDoctorsBySpecialization(spec);
        var freeMap = booking.findFreeSlotsForDoctors(doctors, LocalDate.now(), 14);
        // przygotuj pierwsze 5 slotów na lekarza
        Map<User, List<LocalDateTime>> top5 = new LinkedHashMap<>();
        freeMap.forEach((u, list) -> top5.put(u, list.stream().sorted().limit(5).toList()));

        model.addAttribute("description", description);
        model.addAttribute("aiSpec", spec);
        model.addAttribute("confidence", result.confidence());
        model.addAttribute("options", top5);
        return "booking_ai";
    }

    @PostMapping("/book")
    public String book(@RequestParam("doctorId") Long doctorId,
                       @RequestParam("slot") String slotIso,
                       Principal principal,
                       Model model) {
        User patient = userService.findByEmail(principal.getName());
        User doctor = userService.findById(doctorId);
        if (doctor == null) {
            model.addAttribute("error", "Lekarz nie istnieje");
            return "booking_ai";
        }
        LocalDateTime dt = LocalDateTime.parse(slotIso); // ISO-8601
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorEmail(doctor.getEmail());
        dto.setDate(dt.toLocalDate().toString());
        dto.setTime(dt.toLocalTime().toString());

        try {
            appointmentService.createAppointment(dto, patient);
            return "redirect:/appointments/patient?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "booking_ai";
        }
    }
}