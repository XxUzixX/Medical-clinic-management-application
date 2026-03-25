package com.example.clinic.controller;

import com.example.clinic.dto.UserDto;
import com.example.clinic.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model,
                        @RequestParam(value = "msg", required = false) String msg,
                        @RequestParam(value = "err", required = false) String err) {
        List<UserDto> doctors = userService.findAllDoctors();
        List<UserDto> patients = userService.findAllPatients();
        model.addAttribute("doctors", doctors);
        model.addAttribute("patients", patients);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "admin_users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.deleteById(id);
            ra.addAttribute("msg", "Użytkownik został usunięty.");
        } catch (RuntimeException ex) {
            ra.addAttribute("err", ex.getMessage());
        }
        return "redirect:/admin/users";
    }
}