package com.example.clinic.service;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.entity.Appointment;
import com.example.clinic.entity.User;

import java.util.List;

public interface AppointmentService {
    void createAppointment(AppointmentDto appointmentDto, User patient);

    List<Appointment> getAppointmentsByDoctorId(Long doctorId);

    List<Appointment> getAppointmentsByPatientId(Long patientId);

    void approveAppointment(Long appointmentId);

    void proposeNewTime(Long appointmentId, String newDate, String newTime);

    void confirmAppointment(Long appointmentId, User patient);

    void rejectAppointment(Long appointmentId);
}
