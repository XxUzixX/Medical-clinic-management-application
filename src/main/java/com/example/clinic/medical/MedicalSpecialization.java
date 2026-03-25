package com.example.clinic.medical;

import java.util.Arrays;
import java.util.List;

public enum MedicalSpecialization {
    INTERNISTA("Internista"),
    KARDIOLOG("Kardiolog"),
    DERMATOLOG("Dermatolog"),
    LARYNGOLOG("Laryngolog"),
    PEDIATRA("Pediatra"),
    ORTOPEDA("Ortopeda"),
    NEUROLOG("Neurolog"),
    OKULISTA("Okulista"),
    GINEKOLOG("Ginekolog"),
    PSYCHIATRA("Psychiatra");

    private final String label;

    MedicalSpecialization(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static List<String> asList() {
        return Arrays.stream(values()).map(MedicalSpecialization::getLabel).toList();
    }

    public static boolean isSupported(String v) {
        if (v == null) return false;
        try {
            valueOf(v.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}