package com.example.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_schedule")
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;


    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    // Godziny rozpoczęcia i zakończenia pracy
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    // Zamiana nazwy dni na polskie
    public String getPolishDay() {
        return switch(dayOfWeek) {
            case MONDAY -> "Poniedziałek";
            case TUESDAY -> "Wtorek";
            case WEDNESDAY -> "Środa";
            case THURSDAY -> "Czwartek";
            case FRIDAY -> "Piątek";
            case SATURDAY -> "Sobota";
            case SUNDAY -> "Niedziela";
        };
    }
}