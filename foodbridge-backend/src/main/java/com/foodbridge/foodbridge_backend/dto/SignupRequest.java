package com.foodbridge.foodbridge_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import com.foodbridge.foodbridge_backend.model.UserRole;

@Data
public class SignupRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String mobileNumber;

    @NotBlank
    private String password;

    private UserRole role;

    private String address;

    // Optional NGO specifics
    private String registeredAddress;
    private String contactPersonName;
    private String contactMobileNumber;
    private String ngoPanNumber;
    private String ngoDarpanUniqueId;
    private String registrationCertificateUrl;

    // Optional Homemaker specifics
    private String cuisinesOffered;
    private String experience;
    private String specialization;
    private String idProofUrl;
}
