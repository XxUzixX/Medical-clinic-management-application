package com.example.clinic.service.impl;

import com.example.clinic.entity.DoctorSchedule;
import com.example.clinic.repository.DoctorScheduleRepository;
import com.example.clinic.service.DoctorScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;

    public DoctorScheduleServiceImpl(DoctorScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void saveSchedule(DoctorSchedule schedule) {
        scheduleRepository.save(schedule);
    }

    @Override
    public List<DoctorSchedule> getScheduleForDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}