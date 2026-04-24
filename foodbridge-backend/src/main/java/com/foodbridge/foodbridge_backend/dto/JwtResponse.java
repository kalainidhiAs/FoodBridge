package com.foodbridge.foodbridge_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String mobileNumber;
    private String role;
    
    public JwtResponse(String token, Long id, String mobileNumber, String role) {
        this.token = token;
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.role = role;
    }
}
