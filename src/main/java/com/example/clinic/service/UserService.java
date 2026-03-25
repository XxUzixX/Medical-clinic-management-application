package com.example.clinic.service;

import com.example.clinic.dto.UserDto;
import com.example.clinic.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto, String roleName);
    void deleteById(Long id);
    User findByEmail(String email);

    List<UserDto> findAllUsers();

    List<UserDto> findAllDoctors();
    List<UserDto> findAllPatients();
    User findById(Long id);

}

