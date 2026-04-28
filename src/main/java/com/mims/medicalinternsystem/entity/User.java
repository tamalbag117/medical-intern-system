package com.mims.medicalinternsystem.entity;

import com.mims.medicalinternsystem.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔐 Auth fields
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private int failedAttempts = 0;
    private boolean accountLocked = false;

    // 👤 Profile fields (NEW)
    private String firstName;
    private String lastName;
    private Integer age;

    @Column(length = 10)
    private String phone;
}
