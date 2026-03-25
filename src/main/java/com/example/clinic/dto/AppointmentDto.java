package com.example.clinic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDto {

    private Long id;
    private String doctorEmail;     // Wybrany lekarz przez pacjenta
    private String date;            // Data z formularza
    private String time;            // Godzina z formularza
    private String status;          // Status wizyty


}