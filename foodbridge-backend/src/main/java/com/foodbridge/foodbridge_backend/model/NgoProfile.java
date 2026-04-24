package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class NgoProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String registeredAddress;
    private String contactPersonName;
    private String contactMobileNumber;
    private String ngoPanNumber;
    private String ngoDarpanUniqueId;
    private String registrationCertificateUrl;
}
