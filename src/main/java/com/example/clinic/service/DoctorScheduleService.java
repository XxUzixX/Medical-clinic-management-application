package com.example.clinic.service;

import com.example.clinic.entity.DoctorSchedule;

import java.util.List;

public interface DoctorScheduleService {
    void saveSchedule(DoctorSchedule schedule);
    List<DoctorSchedule> getScheduleForDoctor(Long doctorId);
    void deleteSchedule(Long scheduleId);
}