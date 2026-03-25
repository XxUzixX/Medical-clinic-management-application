package com.example.clinic.service.impl;

import com.example.clinic.dto.UserDto;
import com.example.clinic.entity.Role;
import com.example.clinic.entity.User;
import com.example.clinic.repository.RoleRepository;
import com.example.clinic.repository.UserRepository;
import com.example.clinic.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto, String roleName) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setSpecialization(userDto.getSpecialization() != null ? userDto.getSpecialization() : ""); // Domyślna wartość
        user.setStreet(userDto.getStreet());
        user.setPostalCode(userDto.getPostalCode());
        user.setCity(userDto.getCity());
        user.setState(userDto.getState());
        user.setPhoneNumber(userDto.getPhoneNumber());

        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllDoctors() {
        List<User> doctors = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_DOCTOR")))
                .toList();
        return doctors.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllPatients() {
        List<User> patients = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_USER")))
                .toList();
        return patients.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }


    private UserDto convertEntityToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        String[] name = user.getName().split(" ");
        userDto.setFirstName(name[0]);
        userDto.setLastName(name.length > 1 ? name[1] : "");
        userDto.setEmail(user.getEmail());
        userDto.setSpecialization(user.getSpecialization());
        userDto.setStreet(user.getStreet());
        userDto.setPostalCode(user.getPostalCode());
        userDto.setCity(user.getCity());
        userDto.setState(user.getState());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        return userDto;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    @Override
    public void deleteById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));
        u.getRoles().clear();
        userRepository.save(u);
        userRepository.delete(u);
    }

}
