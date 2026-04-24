package com.foodbridge.foodbridge_backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String mobileNumber;
    private String password;
}
