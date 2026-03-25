package com.example.clinic.service.impl;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.entity.*;
import com.example.clinic.repository.AppointmentRepository;
import com.example.clinic.repository.UserRepository;
import com.example.clinic.service.AppointmentService;
import org.springframework.stereotype.Service;
import com.example.clinic.repository.DoctorScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  DoctorScheduleRepository doctorScheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    private boolean isWithinDoctorSchedule(Long doctorId, LocalDateTime dateTime) {
        // Pobierz grafik lekarza
        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorId(doctorId);
        DayOfWeek dow = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();


        for (DoctorSchedule ds : schedules) {
            if (ds.getDayOfWeek() == dow) {
                if (!time.isBefore(ds.getStartTime()) && time.isBefore(ds.getEndTime())) {
                    return true; // Termin mieści się w jednym z przedziałów
                }
            }
        }
        return false;
    }

    @Override
    public void createAppointment(AppointmentDto appointmentDto, User patient) {
        User doctor = userRepository.findByEmail(appointmentDto.getDoctorEmail());
        if (doctor == null) {
            throw new RuntimeException("Lekarz nie istnieje");
        }

        LocalDate date = LocalDate.parse(appointmentDto.getDate());
        LocalTime time = LocalTime.parse(appointmentDto.getTime());
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // 1. Sprawdź, czy lekarz pracuje w tym dniu/godzinie
        if (!isWithinDoctorSchedule(doctor.getId(), dateTime)) {
            throw new RuntimeException("Lekarz nie pracuje w tym terminie! Wybierz inny dzień/godzinę.");
        }

        // 2. Sprawdź, czy lekarz jest wolny w tym czasie
        if (!isDoctorAvailable(doctor.getId(), dateTime)) {
            throw new RuntimeException("Lekarz ma już zajęty termin: " + dateTime);
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDateTime(dateTime);
        appointment.setStatus(AppointmentStatus.REQUESTED);

        appointmentRepository.save(appointment);
    }

    private boolean isDoctorAvailable(Long doctorId, LocalDateTime start) {

        List<Appointment> existing = appointmentRepository.findByDoctorIdAndDateTimeBetween(doctorId, start, start);
        return existing.isEmpty();
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public void approveAppointment(Long appointmentId) {
        Optional<Appointment> optAppointment = appointmentRepository.findById(appointmentId);
        if (optAppointment.isEmpty()) {
            throw new RuntimeException("Wizyta o ID " + appointmentId + " nie istnieje");
        }
        Appointment appointment = optAppointment.get();
        appointment.setStatus(AppointmentStatus.APPROVED_BY_DOCTOR);
        appointmentRepository.save(appointment);
    }

    @Override
    public void proposeNewTime(Long appointmentId, String newDate, String newTime) {
        Optional<Appointment> optAppointment = appointmentRepository.findById(appointmentId);
        if (optAppointment.isEmpty()) {
            throw new RuntimeException("Wizyta o ID " + appointmentId + " nie istnieje");
        }
        Appointment appointment = optAppointment.get();

        LocalDate date = LocalDate.parse(newDate);
        LocalTime time = LocalTime.parse(newTime);
        LocalDateTime newDateTime = LocalDateTime.of(date, time);

        appointment.setDateTime(newDateTime);
        appointment.setStatus(AppointmentStatus.PROPOSED_NEW_TIME);
        appointmentRepository.save(appointment);
    }

    @Override
    public void confirmAppointment(Long appointmentId, User patient) {
        // Znajdujemy wizytę i upewniamy się, że potwierdza ją poprawny pacjent
        Optional<Appointment> optAppointment = appointmentRepository.findById(appointmentId);
        if (optAppointment.isEmpty()) {
            throw new RuntimeException("Wizyta o ID " + appointmentId + " nie istnieje");
        }
        Appointment appointment = optAppointment.get();
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Tylko pacjent, który umówił wizytę, może ją potwierdzić!");
        }

        // Zmieniamy status na potwierdzony
        appointment.setStatus(AppointmentStatus.CONFIRMED_BY_PATIENT);
        appointmentRepository.save(appointment);
    }

    @Override
    public void rejectAppointment(Long appointmentId) {
        Optional<Appointment> optAppointment = appointmentRepository.findById(appointmentId);
        if (optAppointment.isEmpty()) {
            throw new RuntimeException("Wizyta o ID " + appointmentId + " nie istnieje");
        }
        Appointment appointment = optAppointment.get();
        appointment.setStatus(AppointmentStatus.REJECTED);
        appointmentRepository.save(appointment);
    }

}