// src/main/java/com/example/clinic/service/impl/BookingServiceImpl.java
package com.example.clinic.service.impl;

import com.example.clinic.entity.DoctorSchedule;
import com.example.clinic.entity.User;
import com.example.clinic.repository.AppointmentRepository;
import com.example.clinic.service.BookingService;
import com.example.clinic.service.DoctorScheduleService;
import com.example.clinic.service.UserService;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final DoctorScheduleService scheduleService;
    private final AppointmentRepository appointmentRepository;

    public BookingServiceImpl(UserService userService,
                              DoctorScheduleService scheduleService,
                              AppointmentRepository appointmentRepository) {
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<User> findDoctorsBySpecialization(String specialization) {
        return userService.findAllDoctors().stream()
                .filter(d -> specialization.equalsIgnoreCase(d.getSpecialization()))
                .map(dto -> userService.findByEmail(dto.getEmail()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Map<User, List<LocalDateTime>> findFreeSlotsForDoctors(List<User> doctors, LocalDate from, int days) {
        Map<User, List<LocalDateTime>> result = new LinkedHashMap<>();
        for (User d : doctors) {
            result.put(d, findFreeSlotsForDoctor(d.getId(), from, days));
        }
        return result;
    }

    @Override
    public Optional<Proposal> bestProposal(String specialization, LocalDate from, int days) {
        var doctors = findDoctorsBySpecialization(specialization);
        var map = findFreeSlotsForDoctors(doctors, from, days);
        return map.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(dt -> new Proposal(e.getKey(), dt))).min(Comparator.comparing(Proposal::slot));
    }

    private List<LocalDateTime> findFreeSlotsForDoctor(Long doctorId, LocalDate from, int days) {
        List<LocalDateTime> free = new ArrayList<>();
        LocalDate to = from.plusDays(days);
        for (LocalDate d = from; d.isBefore(to); d = d.plusDays(1)) {
            DayOfWeek dow = d.getDayOfWeek();
            List<DoctorSchedule> dayBlocks = scheduleService.getScheduleForDoctor(doctorId).stream()
                    .filter(s -> s.getDayOfWeek() == dow)
                    .toList();
            if (dayBlocks.isEmpty()) continue;

            for (DoctorSchedule block : dayBlocks) {
                LocalTime t = block.getStartTime();
                while (!t.isAfter(block.getEndTime().minusMinutes(30))) {
                    LocalDateTime candidate = LocalDateTime.of(d, t);
                    if (isFree(doctorId, candidate)) free.add(candidate);
                    t = t.plusMinutes(30);
                }
            }
        }
        return free;
    }

    private boolean isFree(Long doctorId, LocalDateTime dateTime) {
        // sprawdza, czy jest kolizja z istniejącą wizytą (dokładny czas)
        return appointmentRepository
                .findByDoctorIdAndDateTimeBetween(doctorId, dateTime, dateTime)
                .isEmpty();
    }
}