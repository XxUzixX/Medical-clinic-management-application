package com.example.clinic.controller;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.entity.Appointment;
import com.example.clinic.entity.User;
import com.example.clinic.service.AppointmentService;
import com.example.clinic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/new")
    public String showNewAppointmentForm(Model model) {
        List<User> doctors = userService.findAllDoctors().stream()
                .map(dto -> userService.findByEmail(dto.getEmail()))
                .toList();

        model.addAttribute("appointment", new AppointmentDto());
        model.addAttribute("doctors", doctors);
        return "new_appointment";
    }

    @PostMapping("/new")
    public String createAppointment(@ModelAttribute("appointment") AppointmentDto appointmentDto,
                                    Principal principal,
                                    Model model) {
        User patient = userService.findByEmail(principal.getName());
        try {
            appointmentService.createAppointment(appointmentDto, patient);
            return "redirect:/appointments/patient?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "new_appointment";
        }
    }
    //Lista wizyt pacjenta
    @GetMapping("/patient")
    public String getPatientAppointments(Model model, Principal principal) {
        User patient = userService.findByEmail(principal.getName());
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patient.getId());
        model.addAttribute("appointments", appointments);
        return "patient_appointments";
    }

     //Lista wizyt lekarza

    @GetMapping("/doctor")
    public String getDoctorAppointments(Model model, Principal principal) {
        User doctor = userService.findByEmail(principal.getName());
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getId());
        model.addAttribute("appointments", appointments);
        return "doctor_appointments";
    }


     //Lekarz akceptuje wizytę
    @PostMapping("/doctor/approve/{id}")
    public String approveAppointment(@PathVariable("id") Long appointmentId) {
        appointmentService.approveAppointment(appointmentId);
        return "redirect:/appointments/doctor?approved";
    }

    //Lekarz proponuje nowy termin

    @PostMapping("/doctor/propose")
    public String proposeNewTime(@RequestParam("appointmentId") Long appointmentId,
                                 @RequestParam("newDate") String newDate,
                                 @RequestParam("newTime") String newTime) {
        appointmentService.proposeNewTime(appointmentId, newDate, newTime);
        return "redirect:/appointments/doctor?proposed";
    }

    //Pacjent potwierdza termin

    @PostMapping("/patient/confirm/{id}")
    public String confirmAppointment(@PathVariable("id") Long appointmentId,
                                     Principal principal) {
        User patient = userService.findByEmail(principal.getName());
        appointmentService.confirmAppointment(appointmentId, patient);
        return "redirect:/appointments/patient?confirmed";
    }

    //Odrzucenie wizyty
    @PostMapping("/reject/{id}")
    public String rejectAppointment(@PathVariable("id") Long appointmentId, Principal principal) {
        appointmentService.rejectAppointment(appointmentId);

        // Sprawdź w bazie czy użytkownik jest lekarzem, czy pacjentem
        User currentUser = userService.findByEmail(principal.getName());
        boolean isDoctor = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_DOCTOR"));

        if (isDoctor) {
            return "redirect:/appointments/doctor?rejected";
        } else {
            return "redirect:/appointments/patient?rejected";
        }
    }
}