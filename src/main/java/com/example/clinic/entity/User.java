package com.example.clinic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column()
    private String specialization;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column()
    private String street;

    @Column()
    private String postalCode;

    @Column()
    private String city;

    @Column()
    private String state;

    @Column()
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name= "users_roles",
            joinColumns={@JoinColumn(name= "USER_ID", referencedColumnName= "ID")},
            inverseJoinColumns={@JoinColumn(name= "ROLE_ID", referencedColumnName= "ID")})
    private List<Role> roles = new ArrayList<>();
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<DoctorSchedule> schedules = new ArrayList<>();

}
