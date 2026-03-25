package com.example.clinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotEmpty(message = "Imię nie powinno być puste")
    private String firstName;

    @NotEmpty(message = "Nazwisko nie powinno być puste")
    private String lastName;

    private String specialization;

    @NotEmpty(message = "Email nie powinien być pusty")
    @Email(message = "Proszę podać prawidłowy adres email")
    private String email;

    @NotEmpty(message = "Hasło nie powinno być puste")
    @Size(min = 8, message = "Hasło powinno mieć co najmniej 8 znaków")
    private String password;


    @NotEmpty(message = "Numer telefonu nie może być pusty")
    @Pattern(regexp = "\\d{9}", message = "Numer telefonu powinien składać się z 9 cyfr (bez spacji i innych znaków)")
    private String phoneNumber;

    private String street;

    @Pattern(regexp = "^$|^\\d{2}-\\d{3}$", message = "Kod pocztowy w formacie XX-XXX (np. 00-000)")
    private String postalCode;

    private String city;
    private String state;

    private List<String> roles;
}