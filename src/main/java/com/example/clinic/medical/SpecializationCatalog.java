package com.example.clinic.medical;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpecializationCatalog {
    public List<String> all() {
        return MedicalSpecialization.asList();
    }
}