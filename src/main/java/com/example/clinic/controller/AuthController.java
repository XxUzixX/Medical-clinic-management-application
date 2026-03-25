package com.example.clinic.controller;

import com.example.clinic.dto.UserDto;
import com.example.clinic.entity.User;
import com.example.clinic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.clinic.medical.SpecializationCatalog;
import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final SpecializationCatalog specializationCatalog;

    public AuthController(UserService userService, SpecializationCatalog specializationCatalog) {
        this.userService = userService;
        this.specializationCatalog = specializationCatalog;
    }

    @GetMapping("index")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }


    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model) {
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "Konto o podanym adresie e-mail juz istnieje");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user, "ROLE_USER");
        return "redirect:/register?success";
    }

    @PostMapping("/register/doctor/save")
    public String registerDoctor(@Valid @ModelAttribute("doctor") UserDto doctor,
                                 BindingResult result,
                                 Model model) {
        User existing = userService.findByEmail(doctor.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "Konto o podanym adresie e-mail juz istnieje");
        }
        if (result.hasErrors()) {
            model.addAttribute("doctor", doctor);
            return "register_doctor";
        }
        userService.saveUser(doctor, "ROLE_DOCTOR");
        return "redirect:/register/doctor?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin_users";
    }

    @GetMapping("/doctors")
    public String listRegisteredDoctors(Model model) {
        List<UserDto> doctors = userService.findAllDoctors();
        model.addAttribute("doctors", doctors);
        return "doctors";
    }

    @GetMapping("/doctor/patients")
    public String listRegisteredPatients(Model model) {
        List<UserDto> patients = userService.findAllPatients();
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/adminhome")
    public String adminHome() {
        return "adminhome";
    }

    @GetMapping("/doctorhome")
    public String doctorHome() {
        return "doctorhome";
    }

    @GetMapping("/userhome")
    public String userHome() {
        return "userhome";
    }

    @GetMapping("/register/doctor")
    public String showDoctorRegistrationForm(Model model) {
        UserDto doctor = new UserDto();
        model.addAttribute("doctor", doctor);
        model.addAttribute("specializations", specializationCatalog.all());
        return "register_doctor";
    }


}
