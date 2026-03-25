package com.example.clinic.controller;

import com.example.clinic.entity.DoctorSchedule;
import com.example.clinic.entity.User;
import com.example.clinic.service.DoctorScheduleService;
import com.example.clinic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/doctor/schedule")
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;
    private final UserService userService;

    public DoctorScheduleController(DoctorScheduleService scheduleService, UserService userService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @GetMapping
    public String showSchedule(Model model, Principal principal) {
        User doctor = userService.findByEmail(principal.getName());
        List<DoctorSchedule> schedules = scheduleService.getScheduleForDoctor(doctor.getId());
        model.addAttribute("schedules", schedules);
        model.addAttribute("newSchedule", new DoctorSchedule());
        model.addAttribute("daysOfWeek", DayOfWeek.values());

        return "doctor_schedule";
    }

    @PostMapping
    public String addSchedule(@ModelAttribute("newSchedule") DoctorSchedule schedule,
                              @RequestParam("start") String startTime,
                              @RequestParam("end") String endTime,
                              Principal principal,
                              Model model) {
        User doctor = userService.findByEmail(principal.getName());
        schedule.setDoctor(doctor);

        LocalTime start = LocalTime.parse(startTime);
        LocalTime endT = LocalTime.parse(endTime);

        // Zabezpieczenie, by endtime nie był wcześniejszy niż start time
        if (endT.isBefore(start)) {
            model.addAttribute("error", "Godzina zakończenia nie może być wcześniejsza niż rozpoczęcia!");
            // Ponownie wyświetlamy listę 'schedules' i 'dayofweek'
            List<DoctorSchedule> schedules = scheduleService.getScheduleForDoctor(doctor.getId());
            model.addAttribute("schedules", schedules);
            model.addAttribute("daysOfWeek", DayOfWeek.values());
            model.addAttribute("newSchedule", new DoctorSchedule());
            // wracamy do widoku "doctor_schedule.html"
            return "doctor_schedule";
        }

        schedule.setStartTime(start);
        schedule.setEndTime(endT);
        scheduleService.saveSchedule(schedule);
        return "redirect:/doctor/schedule";
    }

    @PostMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable("id") Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return "redirect:/doctor/schedule";
    }

}