package com.example.clinic.ai;

import com.example.clinic.medical.MedicalSpecialization;
import org.springframework.stereotype.Service;

@Service
public class AiTriageService {
    private final GroqService groq;

    public AiTriageService(GroqService groq) {
        this.groq = groq;
    }

    public Result inferSpecialization(String userText) {
        String system = """
            Jesteś triażystą w przychodni. Na podstawie opisu objawów wybierz JEDNĄ specjalizację z listy:
            %s
            Odpowiadaj tylko poprawnym JSON-em, bez komentarzy:
            {"specialization":"<JEDNA_Z_LISTY>","confidence":0.0-1.0}
            Jeśli niepewne preferuj INTERNISTA.
            """.formatted(String.join(", ", MedicalSpecialization.asList()));

        String raw = groq.chat(system, userText);
        String spec = extract(raw, "\"specialization\"\\s*:\\s*\"([^\"]+)\"");
        String conf = extract(raw, "\"confidence\"\\s*:\\s*([0-9.]+)");

        String chosen = (MedicalSpecialization.isSupported(spec)) ?
                spec.toUpperCase() : "INTERNISTA";
        double confidence = 0.0;
        try { confidence = conf != null ? Double.parseDouble(conf) : 0.0; } catch (Exception ignored) {}

        return new Result(chosen, confidence, raw);
    }

    private String extract(String text, String regex) {
        var m = java.util.regex.Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1) : null;
    }

    public record Result(String specialization, double confidence, String rawJson) {}
}