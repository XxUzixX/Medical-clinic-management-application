package com.example.clinic.service;

import com.example.clinic.dto.PrescriptionDto;
import com.example.clinic.entity.User;

import java.util.List;

public interface PrescriptionService {
    void savePrescription(PrescriptionDto prescriptionDto, User doctor, User patient);

    List<PrescriptionDto> findPrescriptionsByPatientId(Long patientId);
    void deleteById(Long prescriptionId);
}
