package com.example.clinic.service.impl;

import com.example.clinic.dto.PrescriptionDto;
import com.example.clinic.entity.Prescription;
import com.example.clinic.entity.User;
import com.example.clinic.repository.PrescriptionRepository;
import com.example.clinic.service.PrescriptionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public void savePrescription(PrescriptionDto prescriptionDto, User doctor, User patient) {
        Prescription prescription = new Prescription();
        prescription.setMedication(prescriptionDto.getMedication());
        prescription.setDosage(prescriptionDto.getDosage());
        prescription.setInstructions(prescriptionDto.getInstructions());
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setIssuedAt(LocalDateTime.now());

        prescriptionRepository.save(prescription);
    }

    @Override
    public List<PrescriptionDto> findPrescriptionsByPatientId(Long patientId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        return prescriptions.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private PrescriptionDto convertEntityToDto(Prescription prescription) {
        PrescriptionDto prescriptionDto = new PrescriptionDto();
        prescriptionDto.setId(prescription.getId());
        prescriptionDto.setMedication(prescription.getMedication());
        prescriptionDto.setDosage(prescription.getDosage());
        prescriptionDto.setInstructions(prescription.getInstructions());
        prescriptionDto.setDoctorName(prescription.getDoctor().getName());
        prescriptionDto.setIssuedAt(prescription.getIssuedAt());
        return prescriptionDto;
    }
    @Override
    public void deleteById(Long id) {
        prescriptionRepository.deleteById(id);
    }
}
