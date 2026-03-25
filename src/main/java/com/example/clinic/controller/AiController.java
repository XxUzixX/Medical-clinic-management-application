package com.example.clinic.controller;

import com.example.clinic.ai.GroqService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ai")
public class    AiController {

    private final GroqService groq;

    public AiController(GroqService groq) {
        this.groq = groq;
    }

    @PostMapping("/summarize-note")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public String summarizeNote(@RequestParam("note") String note, Model model) {
        String system =
      """
       Jesteś asystentem klinicznym.
            ZWRACAJ WYNIK JAKO CZYSTY TEKST (bez Markdown: bez #, *, -, nagłówków MD, kodu, tabel).
            Wyjście ma zawierać DOKŁADNIE trzy sekcje w tej kolejności i z takimi nagłówkami (każdy w osobnej linii):
            Podsumowanie:
            Istotne fakty:
            Pytania na kolejną wizytę:
            
            Reguły:
            - Pod każdym nagłówkiem wypisuj punkty w osobnych liniach, każdy zaczyna się od znaku • (U+2022) i jednej SPACJI (np. "• ").
            - NIE dodawaj żadnych dodatkowych sekcji (np. Zasady, Rekomendacje, Diagnoza, Zalecenia).
            - NIE diagnozuj, NIE spekuluj („może wskazywać”, „prawdopodobnie”), NIE udzielaj porad — tylko porządkuj treść z notatki.
            - Bez JSON i bez nawiasów klamrowych.
            """;;

                        String response = groq.chatText(system, "Streść tę notatkę medyczną po polsku:\n\n" + note);
                        model.addAttribute("summary", response);
                        return "ai_summary";
                    }
                }