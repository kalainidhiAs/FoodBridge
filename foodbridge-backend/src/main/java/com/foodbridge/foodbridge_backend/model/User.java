package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users") // "user" belongs to reserved keywords in some DBs
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String address;

    private String status; // PENDING, APPROVED, REJECTED
}
